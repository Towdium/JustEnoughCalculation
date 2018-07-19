package me.towdium.jecalculation.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.IWidget;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-15.
 * Base panel of GUIs
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WPanel implements IWidget {
    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_PANEL, 0, 0, gui.getXSize(), gui.getYSize(), 5, 5, 5, 5);
    }
}
