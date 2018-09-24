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
    public int y;

    public WLine(int y) {
        this.y = y;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResource(Resource.WGT_LINE, 6, y - 1);
    }
}
