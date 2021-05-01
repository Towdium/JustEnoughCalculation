package me.towdium.jecalculation.event.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiCraft;
import me.towdium.jecalculation.network.ClientHandler;

/**
 * Author: towdium
 * Date:   8/13/17.
 */
@SuppressWarnings("unused")
public class FMLBusEventHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(receiveCanceled = true)
    public void onKey(InputEvent.KeyInputEvent event) {
        if (ClientHandler.keyOpenGuiCraft.isPressed()) Controller.openGuiCraft();
        if (ClientHandler.keyOpenGuiMath.isPressed()) Controller.openGuiMath();
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        JustEnoughCalculation.logger.info("on log out event");
        Controller.writeToLocal();
        LPlaceholder.onLogOut();
    }

    @SubscribeEvent
    public void onGameTick(TickEvent.PlayerTickEvent e) {
        if (e.player.worldObj.isRemote) {
            JecaGui.onGameTick();
        }
    }

}

