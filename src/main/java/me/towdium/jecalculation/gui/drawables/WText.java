package me.towdium.jecalculation.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.IWidget;
import me.towdium.jecalculation.gui.JecGui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

/**
 * Author: towdium
 * Date:   17-8-21.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WText implements IWidget {
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
        String[] text = key.get().split("\n");
        if (xSize == UNDEFINED) gui.drawText(xPos, yPos, font, text);
        else if (ySize == UNDEFINED) gui.drawText(xPos, yPos, xSize, font, text);
        else gui.drawText(xPos, yPos, xSize, ySize, font, text);
    }
}
