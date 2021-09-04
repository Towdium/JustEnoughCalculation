package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.models.DragOffset;
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
    protected int dragConsumerX = 0;
    protected int dragConsumerY = 0;
    protected boolean isDragging = false;

    protected ListenerAction<? super WDrag> dragStartListener = null;
    protected ListenerValue<? super WDrag, DragOffset> dragMoveListener = null;
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

        int newMouseX = JecaGui.getMouseX();
        int newMouseY = JecaGui.getMouseY();
        if (isDragging && dragMoveListener != null && (dragOffsetX != newMouseX || dragOffsetY != newMouseY)) {
            int deltaX = newMouseX - dragOffsetX;
            int deltaY = newMouseY - dragOffsetY;
            System.out.println("cx: " + dragConsumerX + ", cy: " + dragConsumerY);
            System.out.println("nx: " + newMouseX + ", ny: " + newMouseX);
            System.out.println("dox: " + dragOffsetX + ", doy: " + dragOffsetY);
            System.out.println("dx: " + deltaX + ", dy: " + newMouseY);
            System.out.println("x: " + dragConsumerX + deltaX + ", y: " + dragConsumerY + deltaY);
            dragMoveListener.invoke(this, new DragOffset(dragConsumerX + deltaX, dragConsumerY + deltaY));
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

    public WDrag setDragStartListener(ListenerAction<? super WDrag> listener) {
        dragStartListener = listener;
        return this;
    }

    public WDrag setDragMoveListener(ListenerValue<? super WDrag, DragOffset> listener) {
        dragMoveListener = listener;
        return this;
    }

    public WDrag setDragStopListener(ListenerAction<? super WDrag> listener) {
        dragStopListener = listener;
        return this;
    }

    public void setConsumerOffset(int x, int y) {
        this.dragConsumerX = x;
        this.dragConsumerY = y;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!mouseIn(xMouse, yMouse)) {
            return false;
        }
        startDragging();
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

    protected void startDragging() {
        this.dragOffsetX = JecaGui.getMouseX();
        this.dragOffsetY = JecaGui.getMouseY();
        this.isDragging = true;

        if (dragStartListener != null) {
            dragStartListener.invoke(this);
        }
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
}
