package pers.towdium.tudicraft.network;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import pers.towdium.tudicraft.core.Recipe;

/**
 * @author Towdium
 */
public class PlayerHandlerServer implements IPlayerHandler {
    @Override
    public void addRecipe(Recipe recipe) {

    }

    @Override
    public boolean getHasRecipeOf(ItemStack itemStack) {
        return false;
    }

    @Override
    public Recipe getRecipeOf(ItemStack itemStack) {
        return null;
    }

    @Override
    public ImmutableList<Recipe> getAllRecipeOf(ItemStack itemStack) {
        return null;
    }

    @Override
    public int getRecipeIndexOf(ItemStack itemStack) {
        return 0;
    }

    @Override
    public ImmutableList<Integer> getAllRecipeIndexOf(ItemStack itemStack) {
        return null;
    }

    @Override
    public void removeRecipe(int index) {

    }

    @Override
    public void setRecipe(Recipe recipe, int index) {

    }

    @Override
    public Recipe getRecipe(int index) {
        return null;
    }

    @Override
    public ImmutableList<ItemStack> getCostOf(ItemStack itemStack, int amount) {
        return null;
    }
}
