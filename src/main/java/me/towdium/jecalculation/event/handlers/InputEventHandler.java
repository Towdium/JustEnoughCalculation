package me.towdium.jecalculation.event.handlers;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.guis.GuiCalculator;
import me.towdium.jecalculation.network.ProxyClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: towdium
 * Date:   8/13/17.
 */
public class InputEventHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKey(InputEvent.KeyInputEvent event) {
        if (ProxyClient.keyOpenGui.isPressed() && JustEnoughCalculation.side != JustEnoughCalculation.enumSide.SERVER)
            Minecraft.getMinecraft().displayGuiScreen(new GuiCalculator(null));
    }
}
