package me.towdium.jecalculation.event.handlers;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.ControllerClient;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiCalculator;
import me.towdium.jecalculation.network.ClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Author: towdium
 * Date:   8/13/17.
 */
@SuppressWarnings("unused")
public class InputEventHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(receiveCanceled = true)
    public void onKey(InputEvent.KeyInputEvent event) {
        if (ClientHandler.keyOpenGui.isPressed()) {
            if (JustEnoughCalculation.side == JustEnoughCalculation.enumSide.CLIENT) JecaGui.displayGui(new GuiCalculator());
            else Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("chat.server_mode"));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseClick(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        JustEnoughCalculation.logger.info("on gui screen event action performed event pre");
        if (event.gui instanceof JecaGui) {
            JustEnoughCalculation.logger.info("on mouse click in gui screen event");
            JecaGui gui = (JecaGui) event.gui;
            event.setCanceled(gui.handleMouseEvent());
        }
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        JustEnoughCalculation.logger.info("on log out event");
        ControllerClient.writeToLocal();;
    }

}

