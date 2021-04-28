package me.towdium.jecalculation.client.gui.widget.widgets;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.widget.Widget;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-22.
 */
@ParametersAreNonnullByDefault
public class WRectangle extends Widget {
    public int xPos, yPos, xSize, ySize, color;

    public WRectangle(int xPos, int yPos, int xSize, int ySize, int color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.color = color;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawRectangle(xPos + gl(gui), yPos + gt(gui), xSize, ySize, color);
    }
}