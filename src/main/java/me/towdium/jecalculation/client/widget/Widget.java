package me.towdium.jecalculation.client.widget;

import me.towdium.jecalculation.client.gui.JecGui;
import net.minecraft.client.gui.Gui;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
public class Widget extends Gui {
    protected static int gl(JecGui gui) {
        return gui.getGuiLeft();
    }

    protected static int gt(JecGui gui) {
        return gui.getGuiTop();
    }

    public void onDraw(JecGui gui, int xMouse, int yMouse) {
    }

    public static class Advanced extends Widget {
        public void onRemoved(JecGui gui) {
        }

        public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
            return false;
        }

        public boolean onKey(JecGui gui, char ch, int code) {
            return false;
        }

        public void onGuiInit(JecGui gui) {
        }
    }
}
