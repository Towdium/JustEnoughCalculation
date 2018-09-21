package me.towdium.jecalculation.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WButtonText extends WButton { // TODO need rework for text without need for localization
    public static final JecaGui.Font focused = new JecaGui.Font(0xFFFFA0, true, false);
    public static final JecaGui.Font normal = new JecaGui.Font(0xFFFFFF, true, false);
    public String text;

    public WButtonText(int xPos, int yPos, int xSize, int ySize, String name, String text) {
        super(xPos, yPos, xSize, ySize, name);
        this.text = text;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        JecaGui.Font font = mouseIn(xMouse, yMouse) ? focused : normal;
        float x = xPos + Math.max(3, xSize / 2.0f - font.getTextWidth(text) / 2.0f);
        gui.drawText(x, yPos + ySize / 2.0f - font.getTextHeight() / 2.0f, xSize - 6, font, text);
    }
}
