package me.towdium.jecalculation.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.Utilities;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WButtonText extends WButton {
    public WButtonText(int xPos, int yPos, int xSize, int ySize, String name) {
        super(xPos, yPos, xSize, ySize, name);
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        int textColor = mouseIn(xMouse, yMouse) ? 16777120 : 0;
        String text = Utilities.I18n.format(String.join(".", "gui", name, "text"));
        int strWidth = gui.getFontRenderer().getStringWidth(text);
        int ellipsisWidth = gui.getFontRenderer().getStringWidth("...");
        String str = text;
        if (strWidth > xSize - 6 && strWidth > ellipsisWidth)
            str = gui.getFontRenderer().trimStringToWidth(text, xSize - 6 - ellipsisWidth).trim() + "...";
        JecaGui.Font f = JecaGui.Font.DEFAULT_SHADOW.copy();
        f.color = textColor;
        gui.drawText(xPos, yPos, xSize, ySize, f, str);
    }
}
