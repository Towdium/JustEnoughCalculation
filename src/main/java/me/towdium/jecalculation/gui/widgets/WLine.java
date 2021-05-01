package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WLine implements IWidget {
    int xPos, yPos, xSize, ySize;

    public WLine(int y) {
        this(7, y, 162, true);
    }

    public WLine(int xPos, int yPos, int size, boolean horizontal) {
        this.yPos = yPos;
        this.xPos = xPos;
        if (horizontal) {
            xSize = size;
            ySize = 2;
        } else {
            xSize = 2;
            ySize = size;
        }
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_SLOT, xPos, yPos, xSize, ySize, 1);
    }
}
