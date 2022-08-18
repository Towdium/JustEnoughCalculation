package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.gui.JecaGui;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-21.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Environment(EnvType.CLIENT)
public class WText implements IWidget {
    public static final int UNDEFINED = Integer.MAX_VALUE;

    public int xPos, yPos, xSize;
    public boolean centred;
    public JecaGui.FontType font;
    public String key;

    public WText(int xPos, int yPos, JecaGui.FontType font, String key) {
        this(xPos, yPos, UNDEFINED, font, key, false);
    }

    public WText(int xPos, int yPos, int xSize, JecaGui.FontType font, String key, boolean centred) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.font = font;
        this.key = key;
        this.centred = centred;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        int x = xPos + (centred ? xSize / 2 - font.getTextWidth(key) / 2 : 0);
        if (xSize == UNDEFINED) gui.drawText(x, yPos, font, key);
        else gui.drawText(x, yPos, xSize, font, key);
        return false;
    }
}
