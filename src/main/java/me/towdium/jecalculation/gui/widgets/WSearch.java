package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_RED;
import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_WHITE;

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
        for (ISearchable i : lss)
            if (i.setFilter(textField.getText()))
                b = true;
        setColor(b ? COLOR_TEXT_WHITE : COLOR_TEXT_RED);
    }
}
