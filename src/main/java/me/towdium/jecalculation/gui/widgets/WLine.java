package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
