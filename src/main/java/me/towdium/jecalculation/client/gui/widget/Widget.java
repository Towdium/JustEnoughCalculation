package me.towdium.jecalculation.client.gui.widget;

import me.towdium.jecalculation.client.gui.JecGui;
import net.minecraft.client.gui.Gui;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
public class Widget extends Gui {
    protected static int gl(JecGui gui) {
        return gui.getGuiLeft();
    }

    protected static int gt(JecGui gui) {
        return gui.getGuiTop();
    }

    public void onDraw(JecGui gui, int xMouse, int yMouse) {
    }


}
