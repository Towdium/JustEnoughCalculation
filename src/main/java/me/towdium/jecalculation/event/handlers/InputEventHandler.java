package me.towdium.jecalculation.event.handlers;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.guis.GuiCalculator;
import me.towdium.jecalculation.network.ProxyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;

/**
 * Author: towdium
 * Date:   8/13/17.
 */
public class InputEventHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (ProxyClient.keyOpenGui.isPressed() && JustEnoughCalculation.side != JustEnoughCalculation.enumSide.SERVER) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiCalculator(null));
        }
    }

//    @SubscribeEvent(priority = EventPriority.HIGH)
//    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
//        if (event.getGui() instanceof JecGui) {
//            GuiScreen gui = event.getGui();
//            event.setCanceled(((JecGui) gui).handleMouseEvent());
//        }
//    }
}

