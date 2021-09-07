package me.towdium.jecalculation.gui.widgets.models;

public class DragOffset {
    public int newX;
    public int newY;

    public DragOffset(int newX, int newY) {
        this.newX = newX;
        this.newY = newY;
    }

    public int getNewX() {
        return newX;
    }

    public int getNewY() {
        return newY;
    }
}
