package me.towdium.jecalculation.client.gui;

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
