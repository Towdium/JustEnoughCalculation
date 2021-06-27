package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.towdium.jecalculation.gui.Resource.*;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public abstract class WButton extends WTooltip {
    protected int xPos, yPos, xSize, ySize;
    protected ListenerAction<? super WButton> listener;
    protected boolean disabled;
    protected Map<Integer, Boolean> keys = new HashMap<>();

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
        else if (hovered(xMouse, yMouse)) res = WGT_BUTTON_F;
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
        return mouseIn(xMouse, yMouse) || keys.entrySet().stream().anyMatch(Map.Entry::getValue);
    }

    private void trigger() {
        listener.invoke(this);
        Minecraft.getInstance().getSoundHandler().play(
                SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean onKeyPressed(JecaGui gui, int key, int modifier) {
        if (keys.containsKey(key)) {
            keys.put(key, true);
            trigger();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(JecaGui gui, int key, int modifier) {
        if (keys.containsKey(key)) {
            keys.put(key, false);
            return true;
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
        this.keys.clear();
        for (int key : keys) this.keys.put(key, false);
        return this;
    }
}
