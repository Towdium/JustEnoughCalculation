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
public class GuiDisambiguation extends WContainer {
    public GuiDisambiguation(List<List<ItemStack>> iss) {
        ArrayList<ILabel> labels = new ArrayList<>();
        IntStream.range(0, 50).forEach(i -> labels.add(new LabelOreDict(i % 2 == 0 ? "plankWood" : "ingotIron", i)));
        WLabelScroll lsUp = new WLabelScroll(25, 48, 7, 3, WLabel.enumMode.RESULT, true).setLabels(labels);
        WLabelScroll lsDown = new WLabelScroll(25, 105, 7, 3, WLabel.enumMode.RESULT, true).setLabels(labels);
        WTextField tf = new WTextField(25, 24, 90);

        add(new WPanel());
        add(new WSwitcher(7, 7, 162, 5));
        add(new WIcon(149, 24, 20, 20, Resource.ICN_HELP_N, Resource.ICN_HELP_F, "disambiguation.help"));
        add(new WIcon(7, 48, 18, 54, Resource.ICN_LIST_N, Resource.ICN_LIST_F, "disambiguation.list"));
        add(new WIcon(7, 105, 18, 54, Resource.ICN_LABEL_N, Resource.ICN_LABEL_F, "disambiguation.label"));
        add(new WSearch(i -> {
        }, tf, lsUp, lsDown));
    }
}
