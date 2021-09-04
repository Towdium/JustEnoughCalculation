package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-22.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WDrag implements IWidget {
    public int xPos, yPos, xSize, ySize;
    protected int dragOffsetX = 0;
    protected int dragOffsetY = 0;
    protected int dragLastX = 0;
    protected int dragLastY = 0;
    protected boolean isDragging = false;
    protected ListenerValue<? super WDrag, DragDelta> deltaListener = null;
    protected ListenerAction<? super WDrag> dragStopListener = null;

    public WDrag(int xPos, int yPos, int xSize, int ySize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public boolean isDragging() {
        return isDragging;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        boolean isHovering = mouseIn(xMouse, yMouse);
        Resource texture = getResource(isHovering, isDragging);
        gui.drawResourceContinuous(texture, xPos, yPos, xSize, ySize, 0);
        if (isDragging && deltaListener != null && (dragLastX != xMouse || dragLastY != yMouse)) {
            int deltaX = xMouse - this.dragLastX;
            int deltaY = yMouse - this.dragLastY;
            deltaListener.invoke(this, new DragDelta(deltaX, deltaY));
            this.dragLastX = xMouse;
            this.dragLastY = yMouse;
        }
        return false;
    }

    protected Resource getResource(boolean isHovering, boolean isDragging) {
        if (isDragging) {
            return Resource.WGT_DRAG_A;
        }
        if (isHovering) {
            return Resource.WGT_DRAG_F;
        }
        return Resource.WGT_DRAG_N;
    }

    public WDrag setDeltaListener(ListenerValue<? super WDrag, DragDelta> listener) {
        deltaListener = listener;
        return this;
    }

    public WDrag setDragStopListener(ListenerAction<? super WDrag> listener) {
        dragStopListener = listener;
        return this;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!mouseIn(xMouse, yMouse)) {
            return false;
        }
        startDragging(xMouse, yMouse);
        return true;
    }

    @Override
    public boolean onMouseReleased(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!isDragging) {
            return false;
        }
        stopDragging();
        return true;
    }

    protected void startDragging(int xMouse, int yMouse) {
        this.dragOffsetX = xMouse - this.xPos;
        this.dragOffsetY = yMouse - this.yPos;
        this.dragLastX = xMouse;
        this.dragLastY = yMouse;
        this.isDragging = true;
    }

    protected void stopDragging() {
        if (!isDragging) {
            return;
        }

        isDragging = false;
        if (dragStopListener != null) {
            dragStopListener.invoke(this);
        }
    }

    protected boolean mouseIn(int xMouse, int yMouse) {
        return JecaGui.mouseIn(xPos, yPos, xSize, ySize, xMouse, yMouse);
    }

    static class DragDelta {
        public int deltaX;
        public int deltaY;

        public DragDelta(int deltaX, int deltaY) {
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }
    }
}
