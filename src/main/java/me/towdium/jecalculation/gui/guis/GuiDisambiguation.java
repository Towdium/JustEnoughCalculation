package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.guis.pickers.IPicker;
import me.towdium.jecalculation.gui.widgets.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiDisambiguation extends IPicker.Impl implements IGui {
    protected WLabelScroll lsUp;
    protected WLabelScroll lsDown;
    protected WSwitcher switcher;
    protected List<List<ILabel>> record;

    public GuiDisambiguation(List<List<ILabel>> record) {
        this.record = record;
        lsUp = new WLabelScroll(25, 48, 7, 3, WLabel.enumMode.PICKER, true);
        lsDown = new WLabelScroll(25, 105, 7, 3, WLabel.enumMode.PICKER, true);
        switcher = new WSwitcher(7, 7, 162, this.record.size()).setListener(() -> setPage(switcher.getIndex()));
        Consumer<ILabel> consumer = i -> callback.accept(i.copy().multiply(-1));

        add(new WPanel());
        add(new WIcon(7, 48, 18, 54, Resource.ICN_LIST, "disambiguation.list"));
        add(new WIcon(7, 105, 18, 54, Resource.ICN_LABEL, "disambiguation.label"));
        add(new WSearch(25, 24, 90, lsUp.setLsnrUpdate(consumer), lsDown.setLsnrUpdate(consumer)));
        addAll(switcher, lsUp, lsDown);
        setPage(0);
    }

    protected void setPage(int n) {
        lsUp.setLabels(record.get(n));
        lsDown.setLabels(ILabel.CONVERTER.guess(record.get(n)));
    }
}
