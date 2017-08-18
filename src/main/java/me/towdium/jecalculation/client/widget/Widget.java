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

    public void onGuiInit(JecGui gui) {
    }

    public void onDraw(JecGui gui, int xMouse, int yMouse) {
    }

    public void onRemoved(JecGui gui) {
    }

    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        return false;
    }

    public boolean onKey(JecGui gui, char ch, int code) {
        return false;
    }

    public class Timer {
        long time = System.currentTimeMillis();
        boolean running = false;

        public void setState(boolean b) {
            if (!b && running) running = false;
            if (b && !running) {
                running = true;
                time = System.currentTimeMillis();
            }
        }

        public long getTime() {
            return running ? System.currentTimeMillis() - time : 0;
        }

    }
}
