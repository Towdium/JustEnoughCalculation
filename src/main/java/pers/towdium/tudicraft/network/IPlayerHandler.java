package pers.towdium.tudicraft.network;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import pers.towdium.tudicraft.core.Recipe;

import java.util.List;

/**
 * @author Towdium
 */
public interface IPlayerHandler {

    boolean getHasRecipeOf(ItemStack itemStack);

    Recipe getRecipeOf(ItemStack itemStack);

    ImmutableList<Recipe> getAllRecipeOf(ItemStack itemStack);

    int getRecipeIndexOf(ItemStack itemStack);

    ImmutableList<Integer> getAllRecipeIndexOf(ItemStack itemStack);

    void addRecipe(Recipe recipe);

    void removeRecipe(int index);

    void setRecipe(Recipe recipe, int index);

    Recipe getRecipe(int index);

    ImmutableList<ItemStack> getCostOf(ItemStack itemStack, int amount);
}
