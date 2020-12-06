package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
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
    public static final JecaGui.Font focused_u = new JecaGui.Font(0xFFFFA0, true, false, false);
    public static final JecaGui.Font normal_u = new JecaGui.Font(0xFFFFFF, true, false, false);
    public static final JecaGui.Font focused_r = new JecaGui.Font(0xFFFFA0, true, false, true);
    public static final JecaGui.Font normal_r = new JecaGui.Font(0xFFFFFF, true, false, true);
    public String text;
    protected JecaGui.Font focused, normal;

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
        JecaGui.Font font = mouseIn(xMouse, yMouse) ? focused : normal;
        float x = xPos + Math.max(3, xSize / 2.0f - font.getTextWidth(text) / 2.0f);
        gui.drawText(x, yPos + ySize / 2.0f - font.getTextHeight() / 2.0f, xSize - 6, font, text);
        return ret;
    }
}
