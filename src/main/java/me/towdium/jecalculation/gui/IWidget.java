package me.towdium.jecalculation.gui;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IWidget {
    void onDraw(JecGui gui, int xMouse, int yMouse);

    default boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        return false;
    }

    default boolean onKey(JecGui gui, char ch, int code) {
        return false;
    }

    default boolean onScroll(JecGui gui, int xMouse, int yMouse, int diff) {
        return false;
    }
}
