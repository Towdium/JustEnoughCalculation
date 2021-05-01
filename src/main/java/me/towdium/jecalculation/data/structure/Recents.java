package me.towdium.jecalculation.data.structure;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.polyfill.NBTHelper;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Recents {
    Utilities.Recent<ILabel> record = new Utilities.Recent<>((a, b) -> a == ILabel.EMPTY || a.equals(b), 9);

    public Recents(NBTTagList nbt) {
        List<ILabel> ls = StreamSupport.stream(NBTHelper.spliterator(nbt), false)
                                       .filter(n -> n instanceof NBTTagCompound)
                                       .map(n -> ILabel.SERIALIZER.deserialize((NBTTagCompound) n))
                                       .collect(Collectors.toList());
        new Utilities.ReversedIterator<>(ls).forEachRemaining(l -> record.push(l));
    }

    public Recents() {
    }

    public void push(ILabel label) {
        record.push(label);
    }

    public ILabel getLatest() {
        return record.toList().get(0);
    }

    public List<ILabel> getRecords() {
        return record.toList();
    }

    public NBTTagList serialize() {
        NBTTagList ret = new NBTTagList();
        record.toList().forEach(l -> ret.appendTag(ILabel.SERIALIZER.serialize(l)));
        return ret;
    }
}
