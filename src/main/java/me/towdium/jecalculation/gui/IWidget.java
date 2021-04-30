package me.towdium.jecalculation.gui;

import java.util.List;

public interface IWidget {
    void onDraw(JecaGui gui, int xMouse, int yMouse);

    default boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        return false;
    }

    default boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        return false;
    }

    default boolean onKey(JecaGui gui, char ch, int code) {
        return false;
    }

    default boolean onScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        return false;
    }
}
