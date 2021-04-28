package me.towdium.jecalculation.client.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.client.gui.IWidget;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-15.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WPanel implements IWidget {
    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_PANEL, 0, 0, gui.getXSize(), gui.getYSize(), 5, 5, 5, 5);
    }
}
