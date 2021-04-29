package me.towdium.jecalculation.data.structure;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.polyfill.Polyfill;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
public class User {
    public static final String KEY_RECIPE = "recipe";
    public static final String KEY_RECENT = "recent";

    public Recipes recipes;
    public Recent recents;

    public User() {
        recipes = new Recipes();
        recents = new Recent();
    }

    public User(NBTTagCompound nbt) {
        recipes = new Recipes(nbt.getTagList(KEY_RECIPE, 10));
        recents = new Recent(nbt.getTagList(KEY_RECENT, 10));
    }

    public NBTTagCompound serialize() {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag(KEY_RECIPE, recipes.serialize());
        ret.setTag(KEY_RECENT, recents.serialize());
        return ret;
    }

    public List<Recipe> search(String group, ILabel label, Recipe.enumIoType type) {
        Utilities.INDListBuilder<Recipe> builder = new Utilities.INDListBuilder<>();
        IntStream.range(0, type.getSize()).forEach(i -> recipes.get(group).ifPresent(rs -> rs.forEach(r -> {
            ILabel.MERGER.merge(label, r.getLabel(type)[i], true).ifPresent(l -> builder.add(r));
        })));
        return builder.build();
    }

    public static class Recipes {
        public static final String KEY_NAME = "name";
        public static final String KEY_CONTENT = "content";

        Utilities.OrderedHashMap<String, List<Recipe>> records = new Utilities.OrderedHashMap<>();

        public Recipes() {
        }

        public Recipes(NBTTagList nbt) {
            StreamSupport.stream(Polyfill.spliterator(nbt), false).filter(g -> g instanceof NBTTagCompound).forEach(g -> {
                NBTTagCompound group = (NBTTagCompound) g;
                String name = group.getString(KEY_NAME);
                NBTTagList recipes = group.getTagList(KEY_CONTENT, 10);
                StreamSupport.stream(Polyfill.spliterator(recipes), false).filter(r -> r instanceof NBTTagCompound)
                             .map(r -> (NBTTagCompound) r).forEach(r -> add(name, new Recipe(r)));
            });
        }

        public void add(String group, Recipe recipe) {
            records.get(group).orElseGet(() -> {
                ArrayList<Recipe> ret = new ArrayList<>();
                records.put(group, ret);
                return ret;
            }).add(recipe);
        }

        public void set(String group, int index, Recipe recipe) {
            records.get(group).orElseGet(() -> {
                throw new RuntimeException("Group not found: " + group + ".");
            })
                   .set(index, recipe);
        }

        public void remove(String group, int index) {
            records.get(group).orElseGet(() -> {
                throw new RuntimeException("Group not found: " + group + ".");
            })
                   .remove(index);
        }

        public void forEach(BiConsumer<String, List<Recipe>> consumer) {
            records.forEach(consumer);
        }

        public Optional<List<Recipe>> get(String group) {
            return records.get(group);
        }

        public NBTTagList serialize() {
            NBTTagList ret = new NBTTagList();
            forEach((n, rs) -> {
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

    public static class Recent {
        Utilities.Recent<ILabel> record = new Utilities.Recent<>(9);

        public Recent(NBTTagList nbt) {
            List<ILabel> ls = StreamSupport.stream(Polyfill.spliterator(nbt), false)
                                           .filter(n -> n instanceof NBTTagCompound)
                                           .map(n -> ILabel.DESERIALIZER.deserialize((NBTTagCompound) n))
                                           .collect(Collectors.toList());
            new Utilities.ReversedIterator<>(ls).forEachRemaining(l -> record.push(l));
        }

        public Recent() {
        }

        public void push(ILabel label) {
            record.push(label);
        }

        public ILabel getLatest() {
            return record.toList().get(0);
        }

        public List<ILabel> getRecords() {
            List<ILabel> labels = record.toList();
            return labels.subList(1, labels.size());
        }

        public NBTTagList serialize() {
            NBTTagList ret = new NBTTagList();
            record.toList().forEach(l -> ret.appendTag(ILabel.DESERIALIZER.serialize(l)));
            return ret;
        }
    }
}
