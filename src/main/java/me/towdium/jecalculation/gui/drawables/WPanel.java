package me.towdium.jecalculation.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.IWidget;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-15.
 * Base panel of GUIs
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WPanel implements IWidget {
    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_PANEL, 0, 0, gui.getXSize(), gui.getYSize(), 5, 5, 5, 5);
    }
}
