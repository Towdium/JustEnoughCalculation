package me.towdium.jecalculation.client.gui;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
