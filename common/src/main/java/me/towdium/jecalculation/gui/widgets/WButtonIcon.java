package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Environment(EnvType.CLIENT)
public class WButtonIcon extends WButton {
    protected Resource rNormal, rDisabled;

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource.ResourceGroup res, String name) {
        this(xPos, yPos, xSize, ySize, res.one, res.two, name);
    }

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource.ResourceGroup res) {
        this(xPos, yPos, xSize, ySize, res.one, res.two, null);
    }

    private WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal,
                        @Nullable Resource disabled, @Nullable String name) {
        super(xPos, yPos, xSize, ySize, name);
        this.rNormal = normal;
        this.rDisabled = disabled;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        boolean ret = super.onDraw(gui, xMouse, yMouse);
        Resource r = disabled ? rDisabled : rNormal;
        if (r != null) {
            int x = xPos + (xSize - r.getXSize() + 1) / 2;
            int y = yPos + (ySize - r.getYSize() + 1) / 2;
            gui.drawResource(r, x, y);
        }
        return ret;
    }
}
