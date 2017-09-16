package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DButtonIcon extends DButton {
    protected Resource normal, focused;

    public DButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused) {
        this(xPos, yPos, xSize, ySize, normal, focused, null);
    }

    public DButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused, @Nullable String name) {
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
