package pers.towdium.tudicraft.network;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.towdium.tudicraft.core.Recipe;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Towdium
 */
public class PlayerHandlerServer implements IPlayerHandler {
    HashMap<UUID, PlayerHandlerClient> players = new HashMap<>();





    @Override
    public boolean getHasRecipeOf(ItemStack itemStack, UUID uuid) {
        return false;
    }

    @Override
    public Recipe getRecipeOf(ItemStack itemStack, UUID uuid) {
        return null;
    }

    @Override
    public ImmutableList<Recipe> getAllRecipeOf(ItemStack itemStack, UUID uuid) {
        return null;
    }

    @Override
    public int getRecipeIndexOf(ItemStack itemStack, UUID uuid) {
        return 0;
    }

    @Override
    public ImmutableList<Integer> getAllRecipeIndexOf(ItemStack itemStack, UUID uuid) {
        return null;
    }

    @Override
    public void addRecipe(Recipe recipe, UUID uuid) {
        players.get(uuid).addRecipe(recipe, null);
    }

    @Override
    public void removeRecipe(int index, UUID uuid) {
        players.get(uuid).removeRecipe(index, null);
    }

    @Override
    public void setRecipe(Recipe recipe, int index, UUID uuid) {
        players.get(uuid).setRecipe(recipe, index, null);
    }

    @Override
    public Recipe getRecipe(int index, UUID uuid) {
        return null;
    }

    @Override
    public void syncItemCalculator(ItemStack itemIn, String string) {

    }

    @Override
    public void handleLogin(PlayerEvent.LoadFromFile event) {

    }

    @Override
    public void handleSave(PlayerEvent.SaveToFile event) {

    }

    public void addPlayer(UUID uuid){
        players.put(uuid , new PlayerHandlerClient());
    }
}
