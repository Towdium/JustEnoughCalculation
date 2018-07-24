package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.IWPicker;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.drawables.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiDisambiguation extends IWPicker.Simple {
    protected WLabelScroll lsUp;
    protected WLabelScroll lsDown;
    protected WSwitcher switcher;
    protected List<List<ILabel>> record;

    public GuiDisambiguation(List<List<ILabel>> record) {
        lsUp = new WLabelScroll(25, 48, 7, 3, WLabel.enumMode.PICKER, true);
        lsDown = new WLabelScroll(25, 105, 7, 3, WLabel.enumMode.PICKER, true);
        switcher = new WSwitcher(7, 7, 162, this.record.size()).setListener(() -> setPage(switcher.getIndex()));
        this.record = record;
        WTextField tf = new WTextField(25, 24, 90);

        add(new WPanel());
        add(switcher);
        add(new WIcon(149, 24, 20, 20, Resource.ICN_HELP_N, Resource.ICN_HELP_F, "disambiguation.help"));
        add(new WIcon(7, 48, 18, 54, Resource.ICN_LIST_N, Resource.ICN_LIST_F, "disambiguation.list"));
        add(new WIcon(7, 105, 18, 54, Resource.ICN_LABEL_N, Resource.ICN_LABEL_F, "disambiguation.label"));
        add(new WSearch(i -> {
            if (callback != null) callback.accept(i);
        }, tf, lsUp, lsDown));

        setPage(0);
    }

    protected void setPage(int n) {  // TODO
        lsUp.setLabels(record.get(n));
        lsDown.setLabels(ILabel.CONVERTER.guess(record.get(n)));
        //lsUp.setLabels(record.get(n).stream().map(ILabel.CONVERTER_ITEM::toLabel).collect(Collectors.toList()));
        //lsDown.setLabels(ILabel.CONVERTER_ITEM.toLabel(record.get(n)));
    }
}
