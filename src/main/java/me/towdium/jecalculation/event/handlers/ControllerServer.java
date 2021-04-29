package me.towdium.jecalculation.event.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.structure.User;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.UUID;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@ParametersAreNonnullByDefault
public class ControllerServer {
    static HashMap<UUID, Pair<Boolean, User>> records;

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        JustEnoughCalculation.logger.info(String.format("Player join, name: %s, remote: %b, uuid: %s",
                e.player.getDisplayName(), e.player.worldObj.isRemote, e.player.getUniqueID()));
    }

    @SubscribeEvent
    public void onQuit(PlayerEvent.PlayerLoggedOutEvent e) {
        JustEnoughCalculation.logger.info(String.format("Player quit, name: %s, remote: %b",
                e.player.getDisplayName(), e.player.worldObj.isRemote));
    }

    @SubscribeEvent
    public void onLoad(LoadFromFile e) {
        JustEnoughCalculation.logger.info(String.format("Player load, name: %s, remote: %b",
                e.entityPlayer.getDisplayName(), e.entityPlayer.worldObj.isRemote));
    }

    @SubscribeEvent
    public void onSave(SaveToFile e) {
        JustEnoughCalculation.logger.info(String.format("Player save, name: %s, remote: %b",
                e.entityPlayer.getDisplayName(), e.entityPlayer.worldObj.isRemote));
    }
}
