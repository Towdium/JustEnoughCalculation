package me.towdium.jecalculation.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.IWidget;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-15.
 * Base panel of GUIs
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WPanel implements IWidget {
    int xPos, yPos, xSize, ySize;

    public WPanel(int xPos, int yPos, int xSize, int ySize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public WPanel() {
        this(0, 0, 176, 166);
    }


    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_PANEL, xPos, yPos, xSize, ySize, 5, 5, 5, 5);
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        return mouseIn(xMouse, yMouse);
    }

    public boolean mouseIn(int x, int y) {
        int xx = x - xPos;
        int yy = y - yPos;
        return xx >= 0 && xx < xSize && yy >= 0 && yy < ySize;
    }
}
