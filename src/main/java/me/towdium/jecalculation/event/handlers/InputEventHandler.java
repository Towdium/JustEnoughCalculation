package me.towdium.jecalculation.event.handlers;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.guis.GuiCalculator;
import me.towdium.jecalculation.network.ProxyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
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
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (ProxyClient.keyOpenGui.isPressed() && JustEnoughCalculation.side != JustEnoughCalculation.enumSide.SERVER)
            Minecraft.getMinecraft().displayGuiScreen(new GuiCalculator(null));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (event.getGui() instanceof JecGui) {
            GuiScreen gui = event.getGui();
            event.setCanceled(((JecGui) gui).handleMouseEvent());
        }
    }
}
