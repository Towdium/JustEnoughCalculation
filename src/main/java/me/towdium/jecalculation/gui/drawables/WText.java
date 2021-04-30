package me.towdium.jecalculation.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.IWidget;
import me.towdium.jecalculation.gui.JecaGui;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

/**
 * Author: towdium
 * Date:   17-8-21.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WText implements IWidget {
    public static final int UNDEFINED = Integer.MAX_VALUE;

    public int xPos, yPos, xSize;
    public boolean centred;
    public JecaGui.Font font;
    public String key;

    public WText(int xPos, int yPos, JecaGui.Font font, String key) {
        this(xPos, yPos, UNDEFINED, font, key, false);
    }

    public WText(int xPos, int yPos, int xSize, JecaGui.Font font, String key, boolean centred) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.font = font;
        this.key = key;
        this.centred = centred;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        int x = xPos + (centred ? xSize / 2 - font.getTextWidth(key) / 2 : 0);
        if (xSize == UNDEFINED) gui.drawText(x, yPos, font, key);
        else gui.drawText(x, yPos, xSize, font, key);
    }
}
