package me.towdium.jecalculation.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.IWidget;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-22.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
