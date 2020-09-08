package me.towdium.jecalculation.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-22.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WRectangle implements IWidget {
    public int xPos, yPos, xSize, ySize, color;

    public WRectangle(int xPos, int yPos, int xSize, int ySize, int color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.color = color;
    }

    @Override
    public boolean onDraw(MatrixStack matrixStack, JecaGui gui, int xMouse, int yMouse) {
        gui.drawRectangle(matrixStack, xPos, yPos, xSize, ySize, color);
        return false;
    }
}
