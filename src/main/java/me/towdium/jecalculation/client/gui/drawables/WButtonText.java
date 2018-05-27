package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.utils.Utilities.I18n;
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
public class WButtonText extends WButton {
    public WButtonText(int xPos, int yPos, int xSize, int ySize, String name) {
        super(xPos, yPos, xSize, ySize, name);
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        int textColor = mouseIn(xMouse, yMouse) ? 16777120 : 0;
        String text = I18n.format(String.join(".", "gui", name, "text"));
        int strWidth = gui.getFontRenderer().getStringWidth(text);
        int ellipsisWidth = gui.getFontRenderer().getStringWidth("...");
        String str = text;
        if (strWidth > xSize - 6 && strWidth > ellipsisWidth)
            str = gui.getFontRenderer().trimStringToWidth(text, xSize - 6 - ellipsisWidth).trim() + "...";
        JecGui.Font f = JecGui.Font.DEFAULT_SHADOW.copy();
        f.color = textColor;
        gui.drawText(xPos, yPos, xSize, ySize, f, str);
    }
}
