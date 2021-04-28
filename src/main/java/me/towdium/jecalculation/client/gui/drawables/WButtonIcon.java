package me.towdium.jecalculation.client.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WButtonIcon extends WButton {
    protected Resource rNormal, rFocused, rDisabled;

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused) {
        this(xPos, yPos, xSize, ySize, normal, focused, null, null);
    }

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused, String name) {
        this(xPos, yPos, xSize, ySize, normal, focused, null, name);
    }

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused, Resource disabled) {
        this(xPos, yPos, xSize, ySize, normal, focused, disabled, null);
    }

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused,
                       @Nullable Resource disabled, @Nullable String name) {
        super(xPos, yPos, xSize, ySize, name);
        this.rNormal = normal;
        this.rFocused = focused;
        this.rDisabled = disabled;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        Resource r = disabled ? rDisabled : (mouseIn(xMouse, yMouse) ? rFocused : rNormal);
        if (r != null) gui.drawResource(r, xPos + (xSize - r.getXSize()) / 2, yPos + (ySize - r.getYSize()) / 2);
    }

    @Override
    protected List<String> getSuffix() {
        return disabled ? Arrays.asList("disabled", "active") : Collections.singletonList("active");
    }
}
