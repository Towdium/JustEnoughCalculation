package me.towdium.jecalculation.client.gui.drawables;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
public class WButtonIcon extends WButton {
    protected Resource normal, focused;

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused) {
        this(xPos, yPos, xSize, ySize, normal, focused, null);
    }

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused, @Nullable String name) {
        super(xPos, yPos, xSize, ySize, name);
        this.normal = normal;
        this.focused = focused;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        Resource r = mouseIn(xMouse, yMouse) ? focused : normal;
        if (r != null)
            gui.drawResource(r, xPos + (xSize - r.getXSize()) / 2, yPos + (ySize - r.getYSize()) / 2);
    }
}
