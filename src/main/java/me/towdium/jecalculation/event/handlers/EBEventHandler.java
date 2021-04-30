package me.towdium.jecalculation.event.handlers;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraftforge.client.event.GuiScreenEvent;

/**
 * register to MinecraftForge.EVENT_BUS
 */
public class EBEventHandler {
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
}
