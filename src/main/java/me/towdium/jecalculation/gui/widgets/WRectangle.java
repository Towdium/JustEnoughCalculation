package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-22.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WRectangle implements IWidget {
    public int xPos, yPos, xSize, ySize, color;

    public WRectangle(int xPos, int yPos, int xSize, int ySize, int color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.color = color;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawRectangle(xPos, yPos, xSize, ySize, color);
    }
}
