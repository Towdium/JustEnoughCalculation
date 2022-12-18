package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.wrappers.Wrapper;

/**
 * Author: towdium
 * Date:   17-9-15.
 * Base panel of GUIs
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_PANEL_F, xPos, yPos, xSize, ySize, 5, 5, 5, 5);
        return false;
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        return mouseIn(xMouse, yMouse);
    }

    /**
     * Assuming Panel will be the first widget in the container's widgets.
     * @param gui gui
     * @param xMouse mouse x
     * @param yMouse mouse y
     * @param diff different
     * @return true to stop the event
     */
    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        return mouseIn(xMouse, yMouse);
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        boolean ret = mouseIn(xMouse, yMouse);
        if (ret) gui.hand = ILabel.EMPTY;
        return ret;
    }

    @Override
    public boolean getLabelUnderMouse(int xMouse, int yMouse, Wrapper<ILabel> label) {
        return false;
    }

    public boolean mouseIn(int x, int y) {
        int xx = x - xPos;
        int yy = y - yPos;
        return xx >= 0 && xx < xSize && yy >= 0 && yy < ySize;
    }
}
