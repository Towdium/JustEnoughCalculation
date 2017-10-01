package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;
import me.towdium.jecalculation.core.labels.ILabel;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
public class GuiDisambiguation extends WContainer {
    protected WLabelScroll lsUp;
    protected WLabelScroll lsDown;
    protected List<List<ItemStack>> record;

    public GuiDisambiguation(List<List<ItemStack>> record) {
        lsUp = new WLabelScroll(25, 48, 7, 3, WLabel.enumMode.RESULT, true);
        lsDown = new WLabelScroll(25, 105, 7, 3, WLabel.enumMode.RESULT, true);
        this.record = new ArrayList<>(record.stream().filter(iss -> !ILabel.CONVERTER_ITEM.toLabel(iss).isEmpty())
                .collect(Collectors.toSet()));
        WTextField tf = new WTextField(25, 24, 90);

        add(new WPanel());
        add(new WSwitcher(7, 7, 162, this.record.size()).setListener(this::setPage));
        add(new WIcon(149, 24, 20, 20, Resource.ICN_HELP_N, Resource.ICN_HELP_F, "disambiguation.help"));
        add(new WIcon(7, 48, 18, 54, Resource.ICN_LIST_N, Resource.ICN_LIST_F, "disambiguation.list"));
        add(new WIcon(7, 105, 18, 54, Resource.ICN_LABEL_N, Resource.ICN_LABEL_F, "disambiguation.label"));
        add(new WSearch(i -> {
        }, tf, lsUp, lsDown));

        setPage(0);
    }

    protected void setPage(int n) {
        lsUp.setLabels(record.get(n).stream().map(ILabel.CONVERTER_ITEM::toLabel).collect(Collectors.toList()));
        lsDown.setLabels(ILabel.CONVERTER_ITEM.toLabel(record.get(n)));
    }
}
