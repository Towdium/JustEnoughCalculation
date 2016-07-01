package pers.towdium.just_enough_calculation.core;

import net.minecraft.item.ItemStack;
import pers.towdium.just_enough_calculation.util.wrappers.Pair;
import pers.towdium.just_enough_calculation.util.wrappers.Singleton;
import pers.towdium.just_enough_calculation.util.wrappers.Trio;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * Author: Towdium
 * Date:   2016/6/28.
 */

public class PlayerHandlerSP {
    List<ItemStack> oreDictPref = new ArrayList<>();
    LinkedHashMap<String, List<Recipe>> recipes = new LinkedHashMap<>();

    public void addRecipe(Recipe recipe, String group) {
        Singleton<Boolean> i = new Singleton<>(false);
        recipes.forEach((s, recipesList) -> recipesList.forEach(oneRecipe -> {
            if (oneRecipe.equals(recipe))
                i.value = true;
        }));
        if (!i.value) {
            List<Recipe> list = recipes.get(group);
            if (list == null) {
                List<Recipe> buffer = new ArrayList<>();
                buffer.add(recipe);
                recipes.put(group, buffer);
            } else {
                list.add(recipe);
            }
        }
    }

    @Nullable
    public List<Recipe> getRecipeInGroup(String group) {
        return recipes.get(group);
    }

    public String getGroupName(int index) {
        Iterator<Map.Entry<String, List<Recipe>>> it = recipes.entrySet().iterator();
        for (int i = 0; i < index; i++) {
            if (it.hasNext()) {
                it.next();
            } else {
                throw new IndexOutOfBoundsException(index + " out of " + recipes.size());
            }
        }
        if (it.hasNext()) {
            return it.next().getKey();
        } else {
            throw new IndexOutOfBoundsException(index + " out of " + recipes.size());
        }
    }

    public Recipe getRecipe(String group, int index) {
        List<Recipe> temp = recipes.get(group);
        if (temp == null) {
            throw new RuntimeException("Key " + group + " not found in the map.");
        } else {
            return temp.get(index);
        }
    }

    public void removeRecipe(String group, int index) {
        List<Recipe> temp = recipes.get(group);
        if (temp == null) {
            throw new RuntimeException("Key " + group + " not found in the map.");
        } else {
            temp.remove(index);
        }
    }

    public int getSizeRecipe() {
        Singleton<Integer> i = new Singleton<>(0);
        recipes.forEach((s, recipeList) -> i.value += recipeList.size());
        return i.value;
    }

    public int getSizeGroup() {
        return recipes.size();
    }

    public Recipe getRecipeOutput(ItemStack itemStack) {
        Pair<String, Integer> p = getIndexOutput(itemStack).get(0);
        return recipes.get(p.one).get(p.two);
    }

    public List<Pair<String, Integer>> getIndexOutput(ItemStack itemStack) {
        return getIndex(recipe -> recipe.getIndexOutput(itemStack));
    }

    public List<Pair<String, Integer>> getIndexCatalyst(ItemStack itemStack) {
        return getIndex(recipe -> recipe.getIndexCatalyst(itemStack));
    }

    public List<Pair<String, Integer>> getIndexInput(ItemStack itemStack) {
        return getIndex(recipe -> recipe.getIndexInput(itemStack));
    }

    List<Pair<String, Integer>> getIndex(Function<Recipe, Integer> func) {
        Singleton<Integer> i = new Singleton<>(0);
        int size = getSizeRecipe();
        List<Trio<String, Integer, Integer>> bufferA = new ArrayList<>(size);
        recipes.forEach((s, recipeList) -> {
            recipeList.forEach(recipe -> {
                bufferA.add(new Trio<>(s, i.value, func.apply(recipe)));
                ++(i.value);
            });
            i.value = 0;
        });
        bufferA.forEach(element -> i.value = i.value < element.three ? element.three : i.value);
        Collections.reverse(bufferA);
        List<List<Pair<String, Integer>>> bufferB = new ArrayList<>(i.value + 1);
        for (int j = i.value; j >= 0; j--) {
            bufferB.add(new ArrayList<>());
        }
        bufferA.forEach(element -> {
            if (element.three != -1)
                bufferB.get(element.three).add(new Pair<>(element.one, element.two));
        });
        List<Pair<String, Integer>> bufferC = new ArrayList<>(size);
        bufferB.forEach(elementList -> elementList.forEach(bufferC::add));
        return bufferC;
    }
}
