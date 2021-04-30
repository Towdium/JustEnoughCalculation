package me.towdium.jecalculation.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecGui;
import me.towdium.jecalculation.gui.Resource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WIcon extends WTooltip {
    public int xPos, yPos, xSize, ySize;
    public Resource normal, focused;

    public WIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused) {
        this(xPos, yPos, xSize, ySize, normal, focused, null);
    }

    public WIcon(int xPos, int yPos, int xSize, int ySize,
                 Resource normal, Resource focused, @Nullable String name) {
        super(name);
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.normal = normal;
        this.focused = focused;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        gui.drawRectangle(xPos, yPos, xSize, ySize, JecGui.COLOR_GUI_GREY);
        Resource r = mouseIn(xMouse, yMouse) ? focused : normal;
        gui.drawResource(r, (xSize - r.getXSize()) / 2 + xPos, (ySize - r.getYSize()) / 2 + yPos);
    }

    @Override
    public boolean mouseIn(int xMouse, int yMouse) {
        return JecGui.mouseIn(xPos + (xSize - normal.getXSize()) / 2,
                              yPos + (ySize - normal.getYSize()) / 2, normal.getXSize(), normal.getYSize(), xMouse, yMouse);
    }
}
