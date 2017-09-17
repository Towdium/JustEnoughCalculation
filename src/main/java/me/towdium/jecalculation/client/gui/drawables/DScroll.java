package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import org.lwjgl.input.Mouse;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DScroll implements IDrawable {
    public int xPos, yPos, ySize, current;
    public Consumer<Float> lsnrScroll;
    protected boolean drag;

    public DScroll(int xPos, int yPos, int ySize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.ySize = ySize;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        if (Mouse.isButtonDown(0) && drag) setCurrent(yMouse);
        else drag = false;

        gui.drawResourceContinuous(Resource.WGT_SLOT, xPos, yPos, 14, ySize, 3, 3, 3, 3);
        gui.drawResource(Resource.WGT_SCROLL, xPos + 1, yPos + 1 + current);
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        drag = mouseIn(xMouse, yMouse);
        if (drag) setCurrent(yMouse);
        return drag;
    }

    public void setCurrent(int yMouse) {
        current = yMouse - yPos - 9;
        if (current < 0) current = 0;
        if (current > ySize - 17) current = ySize - 17;
        if (lsnrScroll != null) lsnrScroll.accept(current / (ySize - 17f));
    }

    public boolean mouseIn(int xMouse, int yMouse) {
        return JecGui.mouseIn(xPos + 1, yPos + 1, 12, ySize - 2, xMouse, yMouse);
    }

    public DScroll setLsnrScroll(Consumer<Float> lsnrScroll) {
        this.lsnrScroll = lsnrScroll;
        return this;
    }
}
