package me.towdium.jecalculation.client.gui.drawables;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.utils.helpers.LocalizationHelper;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
public class DButtonText extends DButton {
    public DButtonText(int xPos, int yPos, int xSize, int ySize, String name) {
        super(xPos, yPos, xSize, ySize, name);
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        super.onDraw(gui, xMouse, yMouse);
        int textColor = mouseIn(xMouse, yMouse) ? 16777120 : 0;
        String text = LocalizationHelper.format(String.join(".", "gui", name, "text"));
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
