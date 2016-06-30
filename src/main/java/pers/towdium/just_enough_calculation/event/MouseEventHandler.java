package pers.towdium.just_enough_calculation.event;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;

/**
 * Author:  Towdium
 * Created: 2016/6/14.
 */
public class MouseEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (event.getGui() instanceof JECGuiContainer) {
            GuiScreen gui = event.getGui();
            int y = gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;
            int x = Mouse.getEventX() * gui.width / gui.mc.displayWidth;
            event.setCanceled(((JECGuiContainer) gui).handleMouseEvent(x, y));
        }
    }
}