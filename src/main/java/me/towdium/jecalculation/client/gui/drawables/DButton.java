package me.towdium.jecalculation.client.gui.drawables;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.utils.ClientUtils;
import me.towdium.jecalculation.utils.Utilities;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
public abstract class DButton extends  DTooltip {
    protected int xPos, yPos, xSize, ySize;
    protected Runnable lsnrLeft, lsnrRight;
    protected Utilities.Timer timer = new Utilities.Timer();

    public DButton(int xPos, int yPos, int xSize, int ySize, @Nullable String name) {
        super(name);
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public DButton setListenerLeft(Runnable r) {
        lsnrLeft = r;
        return this;
    }

    public DButton setListenerRight(Runnable r) {
        lsnrRight = r;
        return this;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        boolean hovered = JecGui.mouseIn(xPos + 1, yPos + 1, xSize - 2, ySize - 2, xMouse, yMouse);
        gui.drawResourceContinuous(hovered ? Resource.WGT_BUTTON_F : Resource.WGT_BUTTON_N, xPos, yPos,
                xSize, ySize, 3, 3, 3, 3);
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        if (JecGui.mouseIn(xPos + 1, yPos + 1, xSize - 2, ySize - 2, xMouse, yMouse)) {
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
    boolean mouseIn(int xMouse, int yMouse) {
        return JecGui.mouseIn(xPos + 1, yPos + 1, xSize - 2, ySize - 2, xMouse, yMouse);
    }
}
