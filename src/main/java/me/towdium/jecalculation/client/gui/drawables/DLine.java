package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DLine implements IDrawable {
    public int y;

    public DLine(int y) {
        this.y = y;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawResource(Resource.WGT_LINE, 6, y - 1);
    }
}
