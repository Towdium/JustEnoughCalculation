package me.towdium.jecalculation.data.structure;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.polyfill.NBTHelper;
import me.towdium.jecalculation.utils.wrappers.Triple;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Recipes {
    public static final String KEY_NAME = "name";
    public static final String KEY_CONTENT = "content";

    LinkedHashMap<String, List<Recipe>> records = new LinkedHashMap<>();

    public Recipes() {
    }

    public Recipes(NBTTagList nbt) {
        deserialize(nbt);
    }

    public void deserialize(NBTTagList nbt) {
        StreamSupport.stream(NBTHelper.spliterator(nbt), false).filter(g -> g instanceof NBTTagCompound).forEach(g -> {
            NBTTagCompound group = (NBTTagCompound) g;
            String name = group.getString(KEY_NAME);
            NBTTagList recipes = group.getTagList(KEY_CONTENT, 10);
            StreamSupport.stream(NBTHelper.spliterator(recipes), false).filter(r -> r instanceof NBTTagCompound)
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

    public void modify(String group, int index, @Nullable Recipe recipe) {
        if (index == -1) add(group, recipe);
        else if (recipe == null) remove(group, index);
        else set(group, index, recipe);
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

    public Optional<Recipe> getRecipe(ILabel label, Recipe.enumIoType type) {
        return flatStream().map(i -> i.one).filter(i -> i.matches(label, type)).findFirst();
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
