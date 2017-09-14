package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DButton implements IDrawable {
    protected int xPos, yPos, xSize, ySize;
    protected Resource normal, focused;
    protected String tooltip, text;
    protected Runnable lsnrLeft, lsnrRight;
    protected Utilities.Timer timer = new Utilities.Timer();

    public DButton(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused) {
        this(xPos, yPos, xSize, ySize, "", normal, focused, null);
    }

    public DButton(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused, String tooltip) {
        this(xPos, yPos, xSize, ySize, "", normal, focused, tooltip);
    }

    public DButton(int xPos, int yPos, int xSize, int ySize, String text,
                   @Nullable Resource normal, @Nullable Resource focused, @Nullable String tooltip) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.normal = normal;
        this.focused = focused;
        this.tooltip = tooltip;
        this.text = text;
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
        Resource r = hovered ? focused : normal;
        if (r != null)
            gui.drawResource(r, xPos + (xSize - r.getXSize()) / 2, yPos + (ySize - r.getYSize()) / 2);
        if (tooltip != null) {
            timer.setState(hovered);
            if (timer.getTime() > 500) gui.drawTooltip(xMouse, yMouse, gui.localize("tooltip." + tooltip));
        }
        int textColor = hovered ? 16777120 : 0;
        int strWidth = gui.getFontRenderer().getStringWidth(text);
        int ellipsisWidth = gui.getFontRenderer().getStringWidth("...");
        String str = text;
        if (strWidth > xSize - 6 && strWidth > ellipsisWidth)
            str = gui.getFontRenderer().trimStringToWidth(text, xSize - 6 - ellipsisWidth).trim() + "...";
        JecGui.Font f = JecGui.Font.DEFAULT_SHADOW.copy();
        f.color = textColor;
        gui.drawText(xPos, yPos, xSize, ySize, f, str);
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        if (JecGui.mouseIn(xPos + 1, yPos + 1, xSize - 2, ySize - 2, xMouse, yMouse)) {
            if (button == 0 && lsnrLeft != null) {
                lsnrLeft.run();
                Minecraft.getMinecraft().getSoundHandler().playSound(
                        PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            } else if (button == 1 && lsnrRight != null) {
                lsnrLeft.run();
                Minecraft.getMinecraft().getSoundHandler().playSound(
                        PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 0.8F));
                return true;
            }
        }
        return false;
    }
}
