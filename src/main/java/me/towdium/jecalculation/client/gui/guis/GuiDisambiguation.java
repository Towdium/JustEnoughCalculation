package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.core.labels.labels.LabelOreDict;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
public class GuiDisambiguation extends DContainer {
    public GuiDisambiguation(List<List<ItemStack>> iss) {
        ArrayList<ILabel> labels = new ArrayList<>();
        IntStream.range(0, 50).forEach(i -> labels.add(new LabelOreDict(i % 2 == 0 ? "plankWood" : "ingotIron", i)));
        DLabelScroll lsUp = new DLabelScroll(25, 48, 7, 3, DLabel.enumMode.RESULT, true).setLabels(labels);
        DLabelScroll lsDown = new DLabelScroll(25, 105, 7, 3, DLabel.enumMode.RESULT, true).setLabels(labels);
        DTextField tf = new DTextField(25, 24, 90);

        add(new DPanel());
        add(new DSwitcher(7, 7, 162, 5));
        add(new DIcon(149, 24, 20, 20, Resource.ICN_HELP_N, Resource.ICN_HELP_F, "disambiguation.help"));
        add(new DIcon(7, 48, 18, 54, Resource.ICN_LIST_N, Resource.ICN_LIST_F, "disambiguation.list"));
        add(new DIcon(7, 105, 18, 54, Resource.ICN_LABEL_N, Resource.ICN_LABEL_F, "disambiguation.label"));
        add(new DSearch(i -> {
        }, tf, lsUp, lsDown));
    }
}
