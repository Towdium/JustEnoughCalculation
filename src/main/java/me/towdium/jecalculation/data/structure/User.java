package me.towdium.jecalculation.data.structure;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recipe.enumIoType;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Triple;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
public class User {
    public static final String KEY_RECIPE = "recipe";
    public static final String KEY_RECENT = "recent";

    public Recipes recipes;
    public Recent recent;  // client mode only

    public User() {
        recipes = new Recipes();
        recent = new Recent();
    }

    public User(NBTTagCompound nbt) {
        recipes = new Recipes(nbt.getTagList(KEY_RECIPE, 10));
        recent = new Recent(nbt.getTagList(KEY_RECENT, 10));
    }

    public NBTTagCompound serialize() {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag(KEY_RECIPE, recipes.serialize());
        ret.setTag(KEY_RECENT, recent.serialize());
        return ret;
    }

    public static class Recipes {
        public static final String KEY_NAME = "name";
        public static final String KEY_CONTENT = "content";

        LinkedHashMap<String, List<Recipe>> records = new LinkedHashMap<>();

        public Recipes() {
        }

        public Recipes(NBTTagList nbt) {
            StreamSupport.stream(nbt.spliterator(), false).filter(g -> g instanceof NBTTagCompound).forEach(g -> {
                NBTTagCompound group = (NBTTagCompound) g;
                String name = group.getString(KEY_NAME);
                NBTTagList recipes = group.getTagList(KEY_CONTENT, 10);
                StreamSupport.stream(recipes.spliterator(), false).filter(r -> r instanceof NBTTagCompound)
                        .map(r -> (NBTTagCompound) r).forEach(r -> add(name, new Recipe(r)));
            });
        }

        public void add(String group, Recipe recipe) {
            if (records.containsKey(group)) {
                records.get(group).add(recipe);
            } else {
                ArrayList<Recipe> l = new ArrayList<>();
                l.add(recipe);
                records.put(group, l);
            }
        }

        public void set(String group, int index, Recipe recipe) {
            records.get(group).set(index, recipe);
        }

        public int size() {
            return records.size();
        }

        public Stream<Map.Entry<String, List<Recipe>>> stream() {
            return records.entrySet().stream();
        }

        public Stream<Triple<Recipe, String, Integer>> flatStream() {
            return records.entrySet().stream().flatMap(i ->
                    IntStream.range(0, i.getValue().size()).mapToObj(j ->
                            new Triple<>(i.getValue().get(j), i.getKey(), j)));
        }

        public Stream<Triple<Recipe, String, Integer>> flatStream(String group) {
            List<Recipe> l = records.get(group);
            return IntStream.range(0, l.size()).mapToObj(j -> new Triple<>(l.get(j), group, j));
        }

        public void remove(String group, int index) {
            List<Recipe> l = records.get(group);
            l.remove(index);
            if (l.isEmpty()) records.remove(group);
        }

        public Recipe getRecipe(String group, int index) {
            return getGroup(group).get(index);
        }

        public List<Triple<Recipe, String, Integer>> getRecipes() {
            return flatStream().collect(Collectors.toCollection(ArrayList::new));
        }

        public List<Triple<Recipe, String, Integer>> getRecipes(String group) {
            return flatStream(group).collect(Collectors.toCollection(ArrayList::new));
        }

        public List<Triple<Recipe, String, Integer>> getRecipes(ILabel label, enumIoType type) {
            return getRecipes(flatStream(), r -> IntStream.range(0, type.getSize()).anyMatch(i ->
                    ILabel.MERGER.merge(label, r.getLabel(type)[i], true).isPresent()));
        }

        private List<Triple<Recipe, String, Integer>> getRecipes(Stream<Triple<Recipe, String, Integer>> list, Predicate<Recipe> pre) {
            return list.filter(i -> pre.test(i.one)).collect(Collectors.toCollection(ArrayList::new));
        }

        public void forEach(BiConsumer<String, List<Recipe>> consumer) {
            records.forEach(consumer);
        }

        public List<Recipe> getGroup(String group) {
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
        Utilities.Recent<ILabel> record = new Utilities.Recent<>((a, b) ->
                a == ILabel.EMPTY || a.equals(b), 9);
        public static final String IDENTIFIER = "recent";

        public Recent(NBTTagList nbt) {
            List<ILabel> ls = StreamSupport.stream(nbt.spliterator(), false)
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
            return record.toList();
        }

        public NBTTagList serialize() {
            NBTTagList ret = new NBTTagList();
            record.toList().forEach(l -> ret.appendTag(ILabel.DESERIALIZER.serialize(l)));
            return ret;
        }
    }
}
