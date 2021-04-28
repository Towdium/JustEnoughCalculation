package me.towdium.jecalculation.client.gui;

import me.towdium.jecalculation.client.gui.JecGui;

public interface IDrawable {
    static int gl(JecGui gui) {
        return gui.getGuiLeft();
    }

    static int gt(JecGui gui) {
        return gui.getGuiTop();
    }

    void onDraw(JecGui gui, int xMouse, int yMouse);

    default boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        return false;
    }

    default boolean onKey(JecGui gui, char ch, int code) {
        return false;
    }
}
