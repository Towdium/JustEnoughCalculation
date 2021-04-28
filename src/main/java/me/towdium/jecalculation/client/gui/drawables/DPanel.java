package me.towdium.jecalculation.client.gui.drawables;

import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-15.
 */
@ParametersAreNonnullByDefault
public class DPanel implements IDrawable {
    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_PANEL, 0, 0, gui.getXSize(), gui.getYSize(), 5, 5, 5, 5);
    }
}
