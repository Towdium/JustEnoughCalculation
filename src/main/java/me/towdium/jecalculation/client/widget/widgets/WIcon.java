package me.towdium.jecalculation.client.widget.widgets;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.Widget;
import me.towdium.jecalculation.utils.Utilities.Timer;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
public class WIcon extends Widget {
    public int xPos, yPos, xSize, ySize;
    public Resource normal, focused;
    Timer timer = new Timer();

    public WIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.normal = normal;
        this.focused = focused;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        boolean hovered = JecGui.mouseIn(xPos + gl(gui), yPos + gt(gui), xSize, ySize, xMouse, yMouse);
        gui.drawRectangle(xPos + gl(gui), yPos + gt(gui), xSize, ySize,
                hovered ? JecGui.COLOR_BLUE : JecGui.COLOR_GREY);
        Resource r = hovered ? focused : normal;
        gui.drawResource(r, (xSize - r.getXSize()) / 2 + xPos + gl(gui), (ySize - r.getYSize()) / 2 + yPos + gt(gui));
        timer.setState(hovered);
        if (timer.getTime() > 500) gui.drawTooltip(xMouse, yMouse, "hahaa");  // TODO hover
    }
}
