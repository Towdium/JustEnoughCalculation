package me.towdium.jecalculation.core;

import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.StreamSupport;

/**
 * Author: towdium
 * Date:   17-10-6.
 */
public class Record {
    public static final String KEY_NAME = "name";
    public static final String KEY_CONTENT = "content";

    ArrayList<Pair<String, ArrayList<Recipe>>> record = new ArrayList<>();
    HashMap<String, Integer> index = new HashMap<>();

    public Record(NBTTagList nbt) {
        StreamSupport.stream(nbt.spliterator(), false).filter(g -> g instanceof NBTTagCompound).forEach(g -> {
            NBTTagCompound group = (NBTTagCompound) g;
            String name = group.getString(KEY_NAME);
            NBTTagList recipes = group.getTagList(KEY_CONTENT, 10);
            StreamSupport.stream(recipes.spliterator(), false).filter(r -> r instanceof NBTTagCompound)
                    .map(r -> (NBTTagCompound) r).forEach(r -> add(name, new Recipe(r)));
        });
    }

    public void add(String group, Recipe recipe) {
        Integer i = index.get(group);
        if (i == null) {
            record.add(new Pair<>(group, new ArrayList<>()));
            i = record.size() - 1;
        }
        record.get(i).two.add(recipe);
    }

    public void set(String group, int index, Recipe recipe) {
        Integer i = this.index.get(group);
        if (i == null) throw new RuntimeException("Group not found: " + group + ".");
        record.get(i).two.set(index, recipe);
    }

    public void foreach(BiConsumer<String, List<Recipe>> consumer) {
        record.forEach(p -> consumer.accept(p.one, p.two));
    }

    public NBTTagList serialize() {
        NBTTagList ret = new NBTTagList();
        foreach((n, rs) -> {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString(KEY_NAME, n);
            NBTTagList l = new NBTTagList();
            rs.forEach(r -> l.appendTag(r.serialize()));
            nbt.setTag(KEY_CONTENT, l);
            ret.appendTag(nbt);
        });
        return ret;
    }
}
