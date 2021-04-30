package me.towdium.jecalculation.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.IWidget;
import me.towdium.jecalculation.gui.JecGui;
import me.towdium.jecalculation.gui.Resource;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WLine implements IWidget {
    public int y;

    public WLine(int y) {
        this.y = y;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawResource(Resource.WGT_LINE, 6, y - 1);
    }
}
