package me.towdium.jecalculation.client.gui.widget.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.widget.Widget;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

/**
 * Author: towdium
 * Date:   17-8-21.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WText extends Widget {
    public static final int UNDEFINED = Integer.MAX_VALUE;

    public int xPos, yPos, xSize, ySize;
    public JecGui.Font font;
    public Supplier<String> key;

    public WText(int xPos, int yPos, JecGui.Font font, String key) {
        this(xPos, yPos, UNDEFINED, UNDEFINED, font, key);
    }

    public WText(int xPos, int yPos, JecGui.Font font, Supplier<String> key) {
        this(xPos, yPos, UNDEFINED, UNDEFINED, font, key);
    }

    public WText(int xPos, int yPos, int xSize, JecGui.Font font, String key) {
        this(xPos, yPos, xSize, UNDEFINED, font, key);
    }

    public WText(int xPos, int yPos, int xSize, JecGui.Font font, Supplier<String> key) {
        this(xPos, yPos, xSize, UNDEFINED, font, key);
    }

    public WText(int xPos, int yPos, int xSize, int ySize, JecGui.Font font, String key) {
        this(xPos, yPos, xSize, UNDEFINED, font, () -> key);
    }

    public WText(int xPos, int yPos, int xSize, int ySize, JecGui.Font font, Supplier<String> key) {
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
        String[] text = key.get().split("\n");
        if (xSize == UNDEFINED) gui.drawText(xPos + gl, yPos + gt, font, text);
        else if (ySize == UNDEFINED) gui.drawText(xPos + gl, yPos + gt, xSize, font, text);
        else gui.drawText(xPos + gl, yPos + gt, xSize, ySize, font, text);
    }
}
