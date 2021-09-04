package me.towdium.jecalculation.data.structure;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Towdium
 * Date: 19-1-20
 */
public class RecordCraft implements IRecord {
    public static final String KEY_RECENTS = "recents";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_INVENTORY = "inventory";
    public static final String KEY_MODE = "mode";
    public static final String KEY_OVERLAY_OPEN = "overlayOpen";
    public static final String KEY_OVERLAY_X = "overlayPositionX";
    public static final String KEY_OVERLAY_Y = "overlayPositionY";

    Utilities.Recent<ILabel> record = new Utilities.Recent<>((a, b) ->
            a == ILabel.EMPTY || a.equals(b), 9);
    public String amount;
    public boolean inventory;
    public Mode mode;
    public boolean overlayOpen;
    public int overlayPositionX;
    public int overlayPositionY;

    public RecordCraft(CompoundNBT nbt) {
        List<ILabel> ls = nbt.getList(KEY_RECENTS, 10).stream()
                .filter(n -> n instanceof CompoundNBT)
                .map(n -> ILabel.SERIALIZER.deserialize((CompoundNBT) n))
                .collect(Collectors.toList());
        new Utilities.ReversedIterator<>(ls).forEachRemaining(l -> record.push(l, false));
        amount = nbt.getString(KEY_AMOUNT);
        inventory = nbt.getBoolean(KEY_INVENTORY);
        String s = nbt.getString(KEY_MODE);
        mode = Mode.INPUT;
        for (Mode m : Mode.values()) {
            if (s.equals(m.toString().toLowerCase())) mode = m;
        }

        overlayOpen = nbt.getBoolean(KEY_OVERLAY_OPEN);
        overlayPositionX = nbt.getInt(KEY_OVERLAY_X);
        overlayPositionY = nbt.getInt(KEY_OVERLAY_Y);
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

    public CompoundNBT serialize() {
        CompoundNBT ret = new CompoundNBT();
        ret.putBoolean(KEY_INVENTORY, inventory);
        ret.putString(KEY_AMOUNT, amount);
        ListNBT recent = new ListNBT();
        record.toList().forEach(l -> recent.add(ILabel.SERIALIZER.serialize(l)));
        ret.put(KEY_RECENTS, recent);
        ret.putString(KEY_MODE, mode.toString().toLowerCase());
        ret.putBoolean(KEY_OVERLAY_OPEN, overlayOpen);
        ret.putInt(KEY_OVERLAY_X, overlayPositionX);
        ret.putInt(KEY_OVERLAY_Y, overlayPositionY);
        return ret;
    }

    public enum Mode {
        INPUT, OUTPUT, CATALYST, STEPS
    }
}
