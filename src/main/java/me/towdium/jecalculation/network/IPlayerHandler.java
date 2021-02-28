package me.towdium.jecalculation.network;

import com.google.common.collect.ImmutableList;
import me.towdium.jecalculation.core.Recipe;
import net.minecraft.item.ItemStack;

import java.util.UUID;

/**
 * @author Towdium
 */
public interface IPlayerHandler {

    boolean getHasRecipeOf(ItemStack itemStack, UUID uuid);

    Recipe getRecipeOf(ItemStack itemStack, UUID uuid);

    ImmutableList<Recipe> getAllRecipeOf(ItemStack itemStack, UUID uuid);

    int getRecipeIndexOf(ItemStack itemStack, UUID uuid);

    ImmutableList<Integer> getAllRecipeIndexOf(ItemStack itemStack, UUID uuid);

    void addRecipe(Recipe recipe, UUID uuid);

    void removeRecipe(int index, UUID uuid);

    void setRecipe(Recipe recipe, int index, UUID uuid);

    Recipe getRecipe(int index, UUID uuid);

    void syncItemCalculator(ItemStack itemIn, String string);

    void handleLogin(net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile event);

    void handleSave(net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile event);

    boolean removeRecipe(Recipe recipe, UUID uuid);

    boolean containsRecipe(Recipe recipe, UUID uuid);

    boolean getHasRecipe(UUID uuid);

    ImmutableList<Recipe> getAllRecipe(UUID uuid);

    ImmutableList<Integer> getAllRecipeIndex(UUID uuid);
}
