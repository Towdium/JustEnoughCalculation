package me.towdium.jecalculation.gui.widgets;

import static me.towdium.jecalculation.gui.Resource.*;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.ClientUtils;
import org.lwjgl.input.Keyboard;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public abstract class WButton extends WTooltip {
    protected int xPos, yPos, xSize, ySize;
    protected ListenerAction<? super WButton> listener;
    protected boolean disabled;
    protected int[] keys;

    public WButton(int xPos, int yPos, int xSize, int ySize, @Nullable String name) {
        super(name);
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public WButton setListener(ListenerAction<? super WButton> r) {
        listener = r;
        return this;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        boolean hovered = hovered(xMouse, yMouse);
        Resource res;
        if (disabled) res = WGT_BUTTON_D;
        else if (hovered) res = WGT_BUTTON_F;
        else res = WGT_BUTTON_N;

        gui.drawResourceContinuous(res, xPos, yPos, xSize, ySize, 5, 5, 5, 5);
        return hovered;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (mouseIn(xMouse, yMouse) && !disabled && button == 0 && listener != null) {
            trigger();
            return true;
        } else return false;
    }

    protected boolean hovered(int xMouse, int yMouse) {
        if (mouseIn(xMouse, yMouse)) {
            return true;
        }
        if (keys != null) for (int i : keys) if (Keyboard.isKeyDown(i)) return true;
        return false;
    }

    private void trigger() {
        listener.invoke(this);
        ClientUtils.playClickSound(1.0F);
    }

    @Override
    public boolean onKeyPressed(JecaGui gui, char ch, int code) {
        if (keys != null)
            for (int i : keys) {
                if (i == code) {
                    trigger();
                    return true;
                }
            }
        return false;
    }

    @Override
    public boolean mouseIn(int xMouse, int yMouse) {
        return JecaGui.mouseIn(xPos, yPos, xSize - 2, ySize - 2, xMouse, yMouse);
    }

    @SuppressWarnings("UnusedReturnValue")
    public WButton setDisabled(boolean b) {
        disabled = b;
        return this;
    }

    @Override
    protected List<String> getSuffix() {
        return disabled ? Arrays.asList("disabled", "enabled", "") : Arrays.asList("enabled", "");
    }

    public WButton setKeyBind(int... keys) {
        this.keys = keys;
        return this;
    }
}
