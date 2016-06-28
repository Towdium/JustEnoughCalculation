package pers.towdium.justEnoughCalculation.core;

import net.minecraft.item.ItemStack;
import pers.towdium.justEnoughCalculation.core.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Author: Towdium
 * Date:   2016/6/28.
 */

public class PlayerHandlerSP {
    List<Recipe> recipes = new ArrayList<>();
    List<ItemStack> oreDictPref = new ArrayList<>();
    List<String> groupNames = new ArrayList<>();

    public void addRecipe(Recipe recipe) {
        for(Recipe i : recipes){
            if (i.equals(recipe)) {
                return;
            }
        }
        recipes.add(recipe);
    }

    public Recipe getRecipe(int index) {
        return recipes.get(index);
    }

    public void removeRecipe(int index) {
        recipes.remove(index);
    }

    public int getSize() {
        return recipes.size();
    }

    public Recipe getRecipeOutput (ItemStack itemStack) {
        return recipes.get(getIndexOutput(itemStack).get(0));
    }

    public List<Integer> getIndexOutput (ItemStack itemStack) {
        return getIndex(recipe -> recipe.getIndexOutput(itemStack));
    }

    public List<Integer> getIndexCatalyst (ItemStack itemStack) {
        return getIndex(recipe -> recipe.getIndexCatalyst(itemStack));
    }

    public List<Integer> getIndexInput (ItemStack itemStack) {
        return getIndex(recipe -> recipe.getIndexInput(itemStack));
    }

    List<Integer> getIndex (Function<Recipe, Integer> func) {
        List<Integer> bufferA = new ArrayList<>(recipes.size());
        recipes.forEach(recipe -> bufferA.add(func.apply(recipe)));
        int max = 0;
        for(int i : bufferA){
            max = max < i ? i : max;
        }
        List<List<Integer>> bufferB = new ArrayList<>(max+1);
        for(int i = 0; i < max; i++) {
            bufferB.add(new ArrayList<>());
        }
        for(int i = bufferA.size() - 1; i >= 0; i++) {
            if(bufferA.get(i) != -1){
                bufferB.get(bufferA.get(i)).add(i);
            }
        }
        List<Integer> bufferC = new ArrayList<>();
        bufferB.forEach(integers -> integers.forEach(bufferC::add));
        return bufferC;
    }
}
