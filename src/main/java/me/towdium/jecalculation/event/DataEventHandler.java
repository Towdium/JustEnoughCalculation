package me.towdium.jecalculation.event;

import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Author: Towdium
 * Date:   2016/8/11.
 */
public class DataEventHandler {
    @SubscribeEvent
    public void onLogin(PlayerEvent.LoadFromFile event) {
        JustEnoughCalculation.proxy.getPlayerHandler().handleLogin(event);
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        JustEnoughCalculation.proxy.getPlayerHandler().handleJoin(event);
    }

    @SubscribeEvent
    public void onSave(PlayerEvent.SaveToFile event) {
        JustEnoughCalculation.proxy.getPlayerHandler().handleSave(event);
    }
}
