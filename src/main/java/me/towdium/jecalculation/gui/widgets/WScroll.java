package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-8-19.
 * Scroll bar
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WScroll implements IWidget {
    public int xPos, yPos, ySize, current;
    public ListenerAction<? super WScroll> listener;
    protected boolean drag;

    public WScroll(int xPos, int yPos, int ySize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.ySize = ySize;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        if (Mouse.isButtonDown(0) && drag) setCurrent(yMouse - yPos - 9, true);
        else drag = false;

        gui.drawResourceContinuous(Resource.WGT_SLOT, xPos, yPos, 14, ySize, 3, 3, 3, 3);
        gui.drawResource(Resource.WGT_SCROLL, xPos, yPos + current);
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        drag = mouseIn(xMouse, yMouse);
        if (drag) setCurrent(yMouse - yPos - 9, true);
        return drag;
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
