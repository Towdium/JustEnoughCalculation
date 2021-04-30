package me.towdium.jecalculation.gui;

public interface IWidget {
    void onDraw(JecaGui gui, int xMouse, int yMouse);

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
