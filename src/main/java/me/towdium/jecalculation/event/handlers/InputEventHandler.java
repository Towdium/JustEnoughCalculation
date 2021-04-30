package me.towdium.jecalculation.event.handlers;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.JecGui;
import me.towdium.jecalculation.gui.guis.GuiCalculator;
import me.towdium.jecalculation.network.ProxyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.event.GuiScreenEvent;

/**
 * Author: towdium
 * Date:   8/13/17.
 */
public class InputEventHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(receiveCanceled = true)
    public void onKey(InputEvent.KeyInputEvent event) {
        if (ProxyClient.keyOpenGui.isPressed()) {
            if (JustEnoughCalculation.side == JustEnoughCalculation.enumSide.CLIENT) JecGui.displayGui(new GuiCalculator());
            else Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("chat.server_mode"));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseClick(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.gui instanceof JecGui) {
            JustEnoughCalculation.logger.info("on mouse click in gui screen event");
            JecGui gui = (JecGui) event.gui;
            event.setCanceled(gui.handleMouseEvent());
        }
    }

}

