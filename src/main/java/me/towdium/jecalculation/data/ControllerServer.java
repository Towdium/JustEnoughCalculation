package me.towdium.jecalculation.data;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.structure.User;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.UUID;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ControllerServer {
    static HashMap<UUID, Pair<Boolean, User>> records;

    @SubscribeEvent
    public static void onJoin(PlayerLoggedInEvent e) {
        JustEnoughCalculation.logger.info(String.format("Player join, name: %s, remote: %b, uuid: %s",
                e.player.getName(), e.player.world.isRemote, e.player.getUniqueID()));
    }

    @SubscribeEvent
    public static void onQuit(PlayerLoggedOutEvent e) {
        JustEnoughCalculation.logger.info(String.format("Player quit, name: %s, remote: %b",
                e.player.getName(), e.player.world.isRemote));
    }

    @SubscribeEvent
    public static void onLoad(LoadFromFile e) {
        JustEnoughCalculation.logger.info(String.format("Player load, name: %s, remote: %b",
                e.getEntityPlayer().getName(), e.getEntityPlayer().world.isRemote));
    }

    @SubscribeEvent
    public static void onSave(SaveToFile e) {
        JustEnoughCalculation.logger.info(String.format("Player save, name: %s, remote: %b",
                e.getEntityPlayer().getName(), e.getEntityPlayer().world.isRemote));
    }
}
