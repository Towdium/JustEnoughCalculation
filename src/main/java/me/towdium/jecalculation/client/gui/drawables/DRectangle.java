package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-22.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DRectangle implements IDrawable {
    public int xPos, yPos, xSize, ySize, color;

    public DRectangle(int xPos, int yPos, int xSize, int ySize, int color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.color = color;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawRectangle(xPos, yPos, xSize, ySize, color);
    }
}
