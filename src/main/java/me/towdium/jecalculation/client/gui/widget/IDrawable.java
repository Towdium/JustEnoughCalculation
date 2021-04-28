package me.towdium.jecalculation.client.gui.widget;

import me.towdium.jecalculation.client.gui.JecGui;

public class IDrawable extends Widget {
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
