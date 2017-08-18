package me.towdium.jecalculation.client.widget.widgets;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.Widget;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
public class WLine extends Widget {
    protected int y;

    public WLine(int y) {
        this.y = y;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawResource(Resource.WIDGET_LINE, 6 + gui.getGuiLeft(), y + gui.getGuiTop() - 1);
    }
}
