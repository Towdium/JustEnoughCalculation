package me.towdium.jecalculation.data.structure;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.polyfill.NBTHelper;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Author: Towdium
 * Date: 19-1-20
 */
public class RecordCraft implements IRecord {
    public static final String KEY_RECENTS = "recents";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_INVENTORY = "inventory";
    public static final String KEY_MODE = "mode";

    Utilities.Recent<ILabel> record = new Utilities.Recent<>((a, b) -> a == ILabel.EMPTY || a.equals(b), 9);
    public String amount;
    public boolean inventory;
    public Mode mode;

    public RecordCraft(NBTTagCompound nbt) {
        List<ILabel> ls = StreamSupport.stream(NBTHelper.spliterator(nbt.getTagList(KEY_RECENTS, 10)), false)
                                       .filter(n -> n instanceof NBTTagCompound)
                                       .map(n -> ILabel.SERIALIZER.deserialize((NBTTagCompound) n))
                                       .collect(Collectors.toList());
        new Utilities.ReversedIterator<>(ls).forEachRemaining(l -> record.push(l, false));
        amount = nbt.getString(KEY_AMOUNT);
        inventory = nbt.getBoolean(KEY_INVENTORY);
        String s = nbt.getString(KEY_MODE);
        mode = Mode.INPUT;
        for (Mode m : Mode.values()) {
            if (s.equals(m.toString().toLowerCase())) mode = m;
        }
    }

    // return true if any existing matches
    public boolean push(ILabel label, boolean replace) {
        return record.push(label, replace);
    }

    public ILabel getLatest() {
        return record.size() == 0 ? ILabel.EMPTY : record.toList().get(0);
    }

    public List<ILabel> getHistory() {
        return record.size() > 1 ? record.toList().subList(1, record.size()) : new ArrayList<>();
    }

    @Override
    public NBTTagCompound serialize() {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setBoolean(KEY_INVENTORY, inventory);
        ret.setString(KEY_AMOUNT, amount);
        NBTTagList recent = new NBTTagList();
        record.toList().forEach(l -> recent.appendTag(ILabel.SERIALIZER.serialize(l)));
        ret.setTag(KEY_RECENTS, recent);
        ret.setString(KEY_MODE, mode.toString().toLowerCase());
        return ret;
    }

    public enum Mode {
        INPUT, OUTPUT, CATALYST, STEPS
    }
}
