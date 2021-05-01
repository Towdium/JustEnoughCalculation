package me.towdium.jecalculation.data.structure;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.polyfill.NBTHelper;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Recipes {
    LinkedHashMap<String, List<Recipe>> records = new LinkedHashMap<>();

    public Recipes() {
    }

    public Recipes(NBTTagCompound nbt) {
        deserialize(nbt);
    }

    public void deserialize(NBTTagCompound nbt) {
        Set<String> keySet = (Set<String>) nbt.func_150296_c();
        keySet.stream().sorted().forEach(i -> {
            NBTTagList group = nbt.getTagList(i, 10);
            StreamSupport.stream(NBTHelper.spliterator(group), false)
                         .filter(r -> r instanceof  NBTTagCompound)
                         .forEach(r -> add(i, new Recipe((NBTTagCompound) r)));
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

    public Stream<Trio<Recipe, String, Integer>> flatStream() {
        return records.entrySet().stream().flatMap(i ->
                IntStream.range(0, i.getValue().size()).mapToObj(j ->
                        new Trio<>(i.getValue().get(j), i.getKey(), j)));
    }

    public Stream<Trio<Recipe, String, Integer>> flatStream(String group) {
        List<Recipe> l = records.get(group);
        return IntStream.range(0, l.size()).mapToObj(j -> new Trio<>(l.get(j), group, j));
    }

    public void remove(String group, int index) {
        List<Recipe> l = records.get(group);
        l.remove(index);
        if (l.isEmpty()) records.remove(group);
    }

    public Recipe getRecipe(String group, int index) {
        return getGroup(group).get(index);
    }

    public List<Trio<Recipe, String, Integer>> getRecipes() {
        return flatStream().collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Trio<Recipe, String, Integer>> getRecipes(String group) {
        return flatStream(group).collect(Collectors.toCollection(ArrayList::new));
    }

    public Optional<Recipe> getRecipe(ILabel label) {
        return flatStream().map(i -> i.one).filter(i -> i.matches(label)).findFirst();
    }
    public void forEach(BiConsumer<String, List<Recipe>> consumer) {
        records.forEach(consumer);
    }

    public List<Recipe> getGroup(String group) {
        return records.get(group);
    }

    public NBTTagCompound serialize(Collection<String> groups) {
        NBTTagCompound ret = new NBTTagCompound();
        groups.stream().sorted().forEach(i -> {
            NBTTagList l = new NBTTagList();
            getGroup(i).forEach(r -> l.appendTag(r.serialize()));
            ret.setTag(i, l);
        });
        return ret;
    }


    public List<String> getGroups() {
        return records.keySet().stream().sorted().collect(Collectors.toList());
    }

    public NBTTagCompound serialize() {
        return serialize(records.keySet());
    }
}
