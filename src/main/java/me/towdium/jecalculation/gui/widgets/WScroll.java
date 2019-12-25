package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 * Scroll bar
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WScroll implements IWidget {
    public int xPos, yPos, ySize, current;
    boolean active = false;
    public ListenerAction<? super WScroll> listener;

    public WScroll(int xPos, int yPos, int ySize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.ySize = ySize;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_SLOT, xPos, yPos, 14, ySize, 3, 3, 3, 3);
        gui.drawResource(Resource.WGT_SCROLL, xPos, yPos + current);
    }

    @Override
    public boolean onMouseDragged(JecaGui gui, int xMouse, int yMouse, int xDrag, int yDrag) {
        if (mouseIn(xMouse, yMouse) || active) {
            active = true;
            setCurrent(yMouse - yPos - 9, true);
            return true;
        } else return false;
    }

    @Override
    public boolean onMouseReleased(JecaGui gui, int xMouse, int yMouse, int button) {
        active = false;
        return false;
    }

    private void setCurrent(int pos, boolean notify) {
        current = pos;
        if (current < 0) current = 0;
        if (current > ySize - 17) current = ySize - 17;
        if (notify && listener != null) listener.invoke(this);
    }

    public float getCurrent() {
        return current / (ySize - 17f);
    }

    public void setCurrent(float ratio) {
        setCurrent((int) ((ySize - 17) * ratio), false);
    }

    public boolean mouseIn(int xMouse, int yMouse) {
        return JecaGui.mouseIn(xPos + 1, yPos + 1, 12, ySize - 2, xMouse, yMouse);
    }

    public WScroll setListener(@Nullable ListenerAction<? super WScroll> listener) {
        this.listener = listener;
        return this;
    }
}
