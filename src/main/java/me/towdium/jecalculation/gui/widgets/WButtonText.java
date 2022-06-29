package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WButtonText extends WButton {
    public static final JecaGui.FontType focused_u = new JecaGui.FontType(0xFFFFA0, true, false, false);
    public static final JecaGui.FontType normal_u = new JecaGui.FontType(0xFFFFFF, true, false, false);
    public static final JecaGui.FontType focused_r = new JecaGui.FontType(0xFFFFA0, true, false, true);
    public static final JecaGui.FontType normal_r = new JecaGui.FontType(0xFFFFFF, true, false, true);
    public String text;
    protected JecaGui.FontType focused, normal;

    public WButtonText(int xPos, int yPos, int xSize, int ySize, String text) {
        this(xPos, yPos, xSize, ySize, text, null);
    }

    public WButtonText(int xPos, int yPos, int xSize, int ySize, String text, @Nullable String name) {
        this(xPos, yPos, xSize, ySize, text, name, false);
    }

    public WButtonText(int xPos, int yPos, int xSize, int ySize, String text, @Nullable String name, boolean raw) {
        super(xPos, yPos, xSize, ySize, name);
        this.text = text;
        focused = raw ? focused_r : focused_u;
        normal = raw ? normal_r : normal_u;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        boolean ret = super.onDraw(gui, xMouse, yMouse);
        JecaGui.FontType font = mouseIn(xMouse, yMouse) ? focused : normal;
        float x = xPos + Math.max(3, xSize / 2.0f - font.getTextWidth(text) / 2.0f);
        gui.drawText(x, yPos + ySize / 2.0f - font.getTextHeight() / 2.0f, xSize - 6, font, text);
        return ret;
    }
}
