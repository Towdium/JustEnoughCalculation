package pers.towdium.justEnoughCalculation.network;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import pers.towdium.justEnoughCalculation.core.Recipe;

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
    public ImmutableList<Recipe> getAllRecipe(UUID uuid) {
        return null;
    }

    @Override
    public ImmutableList<Integer> getAllRecipeIndex(UUID uuid) {
        return null;
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
        PlayerHandlerClient client = new PlayerHandlerClient();
        client.handleLogin(event);
        players.put(event.entityPlayer.getUniqueID(), client);
        //JustEnoughCalculation.networkWrapper.sendTo(new PacketSyncRecord(client), ((EntityPlayerMP) event.entityPlayer));
    }

    @Override
    public boolean getHasRecipe(UUID uuid) {
        return false;
    }

    @Override
    public void handleSave(PlayerEvent.SaveToFile event) {
        players.get(UUID.fromString(event.playerUUID)).handleSave(event);
    }

    @Override
    public boolean removeRecipe(Recipe recipe, UUID uuid) {
        return false;
    }

    @Override
    public boolean containsRecipe(Recipe recipe, UUID uuid) {
        return false;
    }

    public PlayerHandlerClient getClient(UUID uuid){
        return players.get(uuid);
    }
}
