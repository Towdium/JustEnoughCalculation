package me.towdium.jecalculation.event.handlers;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.guis.GuiCalculator;
import me.towdium.jecalculation.network.ProxyClient;
import net.minecraft.client.Minecraft;

/**
 * Author: towdium
 * Date:   8/13/17.
 */
public class InputEventHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKey(InputEvent.KeyInputEvent event) {
        if (ProxyClient.keyOpenGui.isPressed() && JustEnoughCalculation.side != JustEnoughCalculation.enumSide.SERVER) {
            JustEnoughCalculation.logger.info("key pressed");
            Minecraft.getMinecraft().displayGuiScreen(new GuiCalculator(null));
        }
    }
}

