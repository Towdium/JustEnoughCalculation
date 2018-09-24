package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WButtonIcon extends WButton {
    protected Resource rNormal, rFocused, rDisabled;

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource.ResourceGroup res, String name) {
        this(xPos, yPos, xSize, ySize, res.normal, res.focused, res.disabled, name);
    }

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource.ResourceGroup res) {
        this(xPos, yPos, xSize, ySize, res.normal, res.focused, res.disabled, null);
    }

    private WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused,
                        @Nullable Resource disabled, @Nullable String name) {
        super(xPos, yPos, xSize, ySize, name);
        this.rNormal = normal;
        this.rFocused = focused;
        this.rDisabled = disabled;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        Resource r = disabled ? rDisabled : (mouseIn(xMouse, yMouse) ? rFocused : rNormal);
        if (r != null) gui.drawResource(r, xPos + (xSize - r.getXSize()) / 2, yPos + (ySize - r.getYSize()) / 2);
    }
}
