package me.towdium.jecalculation.client.widget.widgets;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.widget.Widget;

/**
 * Author: towdium
 * Date:   17-8-21.
 */
public class WText extends Widget {
    public static final int UNDEFINED = Integer.MAX_VALUE;

    int xPos, yPos, xSize, ySize;
    JecGui.Font font;
    String key;

    public WText(int xPos, int yPos, JecGui.Font font, String key) {
        this(xPos, yPos, UNDEFINED, UNDEFINED, font, key);
    }

    public WText(int xPos, int yPos, int xSize, JecGui.Font font, String key) {
        this(xPos, yPos, xSize, UNDEFINED, font, key);
    }

    public WText(int xPos, int yPos, int xSize, int ySize, JecGui.Font font, String key) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.font = font;
        this.key = key;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        int gl = gl(gui);
        int gt = gt(gui);
        String[] text = key.split("\n");
        if (xSize == UNDEFINED) gui.drawText(xPos + gl, yPos + gt, font, text);
        else if (ySize == UNDEFINED) gui.drawText(xPos + gl, yPos + gt, xSize, font, text);
        else gui.drawText(xPos + gl, yPos + gt, xSize, ySize, font, text);
    }
}
