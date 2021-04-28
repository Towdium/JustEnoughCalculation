package me.towdium.jecalculation.client.gui.widget.widgets;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.resource.Resource;
import me.towdium.jecalculation.client.gui.widget.Widget;
import me.towdium.jecalculation.utils.Utilities.Timer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
public class WIcon extends Widget {
    public int xPos, yPos, xSize, ySize;
    public Resource normal, focused;
    Timer timer = new Timer();
    public String tooltip;

    public WIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused) {
        this(xPos, yPos, xSize, ySize, normal, focused, null);
    }

    public WIcon(int xPos, int yPos, int xSize, int ySize,
                 Resource normal, Resource focused, @Nullable String tooltip) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.normal = normal;
        this.focused = focused;
        this.tooltip = tooltip;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        boolean hovered = JecGui.mouseIn(xPos + gl(gui) + (xSize - normal.getXSize()) / 2,
                                         yPos + gt(gui) + (ySize - normal.getYSize()) / 2, normal.getXSize(), normal.getYSize(), xMouse, yMouse);
        gui.drawRectangle(xPos + gl(gui), yPos + gt(gui), xSize, ySize, JecGui.COLOR_GREY);
        Resource r = hovered ? focused : normal;
        gui.drawResource(r, (xSize - r.getXSize()) / 2 + xPos + gl(gui), (ySize - r.getYSize()) / 2 + yPos + gt(gui));
        if (tooltip != null) {
            timer.setState(hovered);
            if (timer.getTime() > 500) gui.drawTooltip(xMouse, yMouse, gui.localize("tooltip." + tooltip));
        }
    }
}
