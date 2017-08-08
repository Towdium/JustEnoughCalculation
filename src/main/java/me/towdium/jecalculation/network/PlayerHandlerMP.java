package me.towdium.jecalculation.network;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.core.Recipe;
import me.towdium.jecalculation.network.packets.PacketRecordSync;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Author: Towdium
 * Date:   2016/8/11.
 */
public class PlayerHandlerMP implements IProxy.IPlayerHandler {
    HashMap<UUID, PlayerHandlerSP> records = new HashMap<>();

    public PlayerHandlerSP getData(UUID uuid) {
        PlayerHandlerSP ret = records.get(uuid);
        if (ret == null)
            throw new IllegalArgumentException("UUID not found");
        else
            return ret;
    }

    public void addRecipe(UUID uuid, String group, Recipe recipe) {
        getData(uuid).addRecipe(recipe, group);
    }

    public void setRecipe(UUID uuid, String group, String groupOld, int index, Recipe recipe) {
        getData(uuid).setRecipe(group, groupOld, index, recipe);
    }

    public void removeRecipe(UUID uuid, String group, int index) {
        getData(uuid).removeRecipe(group, index);
    }

    public void addOredictPref(UUID uuid, ItemStack stack) {
        getData(uuid).addOreDictPref(stack);
    }

    public void removeOredictPref(UUID uuid, ItemStack stack) {
        getData(uuid).removeOreDictPref(stack);
    }

    @Override
    public void handleLogin(PlayerEvent.LoadFromFile event) {
        PlayerHandlerSP handler = new PlayerHandlerSP();
        handler.handleLogin(event);
        records.put(event.getEntityPlayer().getUniqueID(), handler);
    }

    @Override
    public void handleSave(PlayerEvent.SaveToFile event) {
        getData(event.getEntityPlayer().getUniqueID()).handleSave(event);
    }

    @Override
    public void handleJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP)
            JustEnoughCalculation.networkWrapper.sendTo(new PacketRecordSync(getData(event.getEntity().getUniqueID())), ((EntityPlayerMP) event.getEntity()));
    }
}
