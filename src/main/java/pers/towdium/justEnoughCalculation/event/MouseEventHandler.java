package pers.towdium.justEnoughCalculation.event;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import pers.towdium.justEnoughCalculation.gui.JECGuiContainer;

/**
 * Author:  Towdium
 * Created: 2016/6/14.
 */
public class MouseEventHandler {
    long timeLast = 0;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (event.getGui() instanceof JECGuiContainer) {
            if(System.currentTimeMillis()-timeLast > 100){
                GuiScreen gui = event.getGui();
                int y = gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;
                int x = Mouse.getEventX() * gui.width / gui.mc.displayWidth;
                timeLast = Mouse.isButtonDown(0) ? System.currentTimeMillis() : timeLast;
                ((JECGuiContainer) gui).handleMouseEvent(event, x, y);
            } else {
                event.setCanceled(true);
            }
        }
    }
}