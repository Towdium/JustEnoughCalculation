package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.towdium.jecalculation.gui.Resource.*;

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

    protected Resource getDisabled() {
        return WGT_BUTTON_D;
    }

    protected Resource getNormal() {
        return WGT_BUTTON_N;
    }

    protected Resource getFocused() {
        return WGT_BUTTON_F;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        boolean hovered = JecaGui.mouseIn(xPos + 1, yPos + 1, xSize - 2, ySize - 2, xMouse, yMouse);
        gui.drawResourceContinuous(disabled ? getDisabled() : (hovered ? getFocused() : getNormal())
                , xPos, yPos, xSize, ySize, 5, 5, 5, 5);
    }

    @Override
    public boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!disabled && JecaGui.mouseIn(xPos + 1, yPos + 1, xSize - 2, ySize - 2, xMouse, yMouse)) {
            if (button == 0 && listener != null) {
                listener.invoke(this);
                Minecraft.getMinecraft().getSoundHandler().playSound(
                        PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseIn(int xMouse, int yMouse) {
        return JecaGui.mouseIn(xPos + 1, yPos + 1, xSize - 2, ySize - 2, xMouse, yMouse);
    }

    @SuppressWarnings("UnusedReturnValue")
    public WButton setDisabled(boolean b) {
        disabled = b;
        return this;
    }

    @Override
    protected List<String> getSuffix() {
        return disabled ? Arrays.asList("disabled", "active") : Collections.singletonList("active");
    }
}
