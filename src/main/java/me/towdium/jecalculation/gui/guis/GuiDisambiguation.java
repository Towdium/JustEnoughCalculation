package me.towdium.jecalculation.gui.guis;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.guis.pickers.IPicker;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.data.label.ILabel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

import static me.towdium.jecalculation.gui.Resource.ICN_LABEL;
import static me.towdium.jecalculation.gui.Resource.ICN_LIST;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiDisambiguation extends IPicker.Impl implements IGui {
    protected WLabelScroll lsUp = new WLabelScroll(25, 48, 7, 3, WLabel.Mode.PICKER, true);
    protected WLabelScroll lsDown = new WLabelScroll(25, 105, 7, 3, WLabel.Mode.PICKER, true);
    protected List<List<ILabel>> record;

    public GuiDisambiguation(List<List<ILabel>> record) {
        this.record = record;
        WSwitcher switcher = new WSwitcher(7, 7, 162, this.record.size()).setListener(i -> setPage(i.getIndex()));
        ListenerValue<IWidget, ILabel> consumer = (i, v) -> callback.accept(v.copy().multiply(-1));
        add(new WPanel());
        add(new WIcon(7, 48, 18, 54, ICN_LIST, "disambiguation.list"));
        add(new WIcon(7, 105, 18, 54, ICN_LABEL, "disambiguation.label"));
        add(new WSearch(25, 24, 90, lsUp.setListener(consumer), lsDown.setListener(consumer)));
        addAll(switcher, lsUp, lsDown);
        setPage(0);
    }


    protected void setPage(int n) {
        lsUp.setLabels(record.get(n));
        lsDown.setLabels(ILabel.CONVERTER.guess(record.get(n)));
    }
}
