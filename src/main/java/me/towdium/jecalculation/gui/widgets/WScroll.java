package me.towdium.jecalculation.gui.widgets;

import static me.towdium.jecalculation.gui.Resource.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;

/**
 * Author: towdium
 * Date: 17-8-19.
 * Scroll bar
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WScroll implements IWidget {

    public int xPos, yPos, ySize;
    public float current;
    boolean active = false;
    float step = 0f;
    public ListenerAction<? super WScroll> listener;
    int height;

    public WScroll(int xPos, int yPos, int ySize) {
        height = 17;
        this.xPos = xPos;
        this.yPos = yPos;
        this.ySize = ySize;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        if (Mouse.isButtonDown(0) && active) setCurrent(yMouse - yPos - height / 2, true);
        else active = false;

        int offset = (int) (current * (ySize - height));
        boolean in = mouseIn(xMouse, yMouse);
        Resource r = in ? WGT_SCROLL_F : WGT_SCROLL_N;

        gui.drawResourceContinuous(WGT_SLOT, xPos, yPos, 14, ySize, 3, 3, 3, 3);
        gui.drawResourceContinuous(r, xPos, yPos + offset, 14, height, 3);
        return false;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        active = mouseIn(xMouse, yMouse);
        if (active) setCurrent(yMouse - yPos - height / 2, true);
        return active;
    }

    @Override
    public boolean onMouseReleased(JecaGui gui, int xMouse, int yMouse, int button) {
        active = false;
        return false;
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        boolean in = mouseIn(xMouse, yMouse);
        if (in) setCurrent(getCurrent() - diff * step, true);
        return in;
    }

    public WScroll setStep(float step) {
        this.step = step;
        return this;
    }

    public WScroll setRatio(float ratio) {
        height = Math.max((int) ((ySize - 6) * ratio), 2) + 6;
        return this;
    }

    private void setCurrent(int pos, boolean notify) {
        setCurrent(pos / (float) (ySize - height), notify);
    }

    public float getCurrent() {
        return current;
    }

    public WScroll setCurrent(float ratio, boolean notify) {
        // setCurrent((int) ((ySize - height) * ratio), false);
        current = ratio;
        if (current < 0) current = 0;
        if (current > 1) current = 1;
        if (notify && listener != null) listener.invoke(this);
        return this;
    }

    public WScroll setCurrent(float ratio) {
        setCurrent(ratio, false);
        return this;
    }

    public boolean mouseIn(int xMouse, int yMouse) {
        return JecaGui.mouseIn(xPos + 1, yPos + 1, 12, ySize - 2, xMouse, yMouse);
    }

    public WScroll setListener(@Nullable ListenerAction<? super WScroll> listener) {
        this.listener = listener;
        return this;
    }
}
