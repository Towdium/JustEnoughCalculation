package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WSearch extends WTextField {
    ISearchable[] lss;

    public WSearch(int xPos, int yPos, int xSize, ISearchable... lss) {
        super(xPos, yPos, xSize);
        this.lss = lss;
    }

    @Override
    protected void notifyLsnr() {
        super.notifyLsnr();
        refresh();
    }

    public void refresh() {
        boolean b = false;
        for (ISearchable i : lss) if (i.setFilter(textField.getText())) b = true;
        setColor(b ? JecaGui.COLOR_TEXT_WHITE : JecaGui.COLOR_TEXT_RED);
    }
}
