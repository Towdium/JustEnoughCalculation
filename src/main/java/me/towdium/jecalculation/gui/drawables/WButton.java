package me.towdium.jecalculation.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.ClientUtils;
import me.towdium.jecalculation.utils.Utilities;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public abstract class WButton extends WTooltip {
    protected int xPos, yPos, xSize, ySize;
    protected Runnable lsnrLeft, lsnrRight;
    protected Utilities.Timer timer = new Utilities.Timer();
    protected boolean disabled;

    public WButton(int xPos, int yPos, int xSize, int ySize, @Nullable String name) {
        super(name);
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public WButton setListenerLeft(Runnable r) {
        lsnrLeft = r;
        return this;
    }

    public WButton setListenerRight(Runnable r) {
        lsnrRight = r;
        return this;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        boolean hovered = JecGui.mouseIn(xPos + 1, yPos + 1, xSize - 2, ySize - 2, xMouse, yMouse);
        gui.drawResourceContinuous(disabled ? Resource.WGT_BUTTON_D :
                                   (hovered ? Resource.WGT_BUTTON_F : Resource.WGT_BUTTON_N), xPos, yPos, xSize, ySize, 3, 3, 3, 3);
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        if (!disabled && JecGui.mouseIn(xPos + 1, yPos + 1, xSize - 2, ySize - 2, xMouse, yMouse)) {
            if (button == 0 && lsnrLeft != null) {
                lsnrLeft.run();
                ClientUtils.playClickSound(1.0F);
                return true;
            } else if (button == 1 && lsnrRight != null) {
                lsnrLeft.run();
                ClientUtils.playClickSound(0.8F);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseIn(int xMouse, int yMouse) {
        return JecGui.mouseIn(xPos + 1, yPos + 1, xSize - 2, ySize - 2, xMouse, yMouse);
    }

    public WButton setDisabled(boolean b) {
        disabled = b;
        return this;
    }
}
