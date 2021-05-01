package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.data.label.ILabel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
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
