package me.towdium.jecalculation.data.structure;

import me.towdium.jecalculation.JecaConfig;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.polyfill.NBTHelper;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ParametersAreNonnullByDefault
public class Recipes {
    LinkedHashMap<String, List<Recipe>> records = new LinkedHashMap<>();
    HashSet<Recipe> cache = new HashSet<>();

    public Recipes() {
        File file = JecaConfig.defaultFile;
        NBTTagCompound nbt = Utilities.Json.read(file);
        if (nbt == null) JustEnoughCalculation.logger.info("Failed to load default records at " + file + ".");
        else {
            JustEnoughCalculation.logger.info("Loading default records at " + file + ".");
            deserialize(nbt);
        }
    }

    public Recipes(NBTTagCompound nbt) {
        deserialize(nbt);
    }

    protected void deserialize(NBTTagCompound nbt) {
        Set<String> keySet = (Set<String>) nbt.func_150296_c();
        keySet.stream().sorted().forEach(i -> {
            NBTTagList group = nbt.getTagList(i, 10);
            StreamSupport.stream(NBTHelper.spliterator(group), false)
                         .filter(r -> r instanceof  NBTTagCompound)
                         .forEach(r -> {
                             try {
                                 add(i, new Recipe((NBTTagCompound) r));
                             } catch (IllegalArgumentException e) {
                                 JustEnoughCalculation.logger.warn("Invalid recipe record :" + r);
                             }
                         });
        });
    }

    public void add(String group, Recipe recipe) {
        records.computeIfAbsent(group, k -> new ArrayList<>()).add(recipe);
        cache.add(recipe);
    }

    public void modify(String group, int index, @Nullable Recipe recipe) {
        if (index == -1 && recipe != null) add(group, recipe);
        else if (recipe == null) remove(group, index);
        else set(group, index, recipe);
    }

    public void set(String group, int index, Recipe recipe) {
        Recipe r = records.get(group).set(index, recipe);
        cache.remove(r);
        cache.add(recipe);
    }

    public int size() {
        return records.size();
    }

    public Stream<Pair<String, List<Recipe>>> stream() {
        return records.entrySet().stream().map(i -> new Pair<>(i.getKey(), i.getValue()));
    }

    public void remove(String group, int index) {
        List<Recipe> l = records.get(group);
        cache.remove(l.remove(index));
        if (l.isEmpty()) records.remove(group);
    }

    public Recipe getRecipe(String group, int index) {
        return getGroup(group).get(index);
    }

    public List<Recipe> getRecipes() {
        return records.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Recipe> getRecipes(String group) {
        return records.get(group);
    }

    public void forEach(BiConsumer<String, List<Recipe>> consumer) {
        records.forEach(consumer);
    }

    /**
     * Do not modify return value!
     *
     * @param group Name of group to get
     * @return List of Recipes in group
     */
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
        return new ArrayList<>(records.keySet());
    }

    public NBTTagCompound serialize() {
        return serialize(records.keySet());
    }

    public RecipeIterator recipeIterator() {
        return new RecipeIterator();
    }

    public RecipeIterator recipeIterator(String group) {
        return new RecipeIterator(group);
    }

    public class RecipeIterator implements Iterator<Recipe> {
        Iterator<Map.Entry<String, List<Recipe>>> i;
        Iterator<Recipe> j;

        public RecipeIterator() {
            i = records.entrySet().iterator();
        }

        public RecipeIterator(String group) {
            HashMap<String, List<Recipe>> tmp = new HashMap<>();
            tmp.put(group, records.get(group));
            i = tmp.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            while (j == null || !j.hasNext()) {
                if (i.hasNext()) j = i.next().getValue().iterator();
                else return false;
            }
            return j.hasNext();
        }

        @Override
        public Recipe next() {
            while (j == null || !j.hasNext()) if (i.hasNext()) j = i.next().getValue().iterator();
            return j.next();
        }

        public Stream<Recipe> stream() {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
        }
    }

    public boolean hasDuplicate(Recipe r) {
        return cache.contains(r);
    }
}
