package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.drawables.DContainer;
import me.towdium.jecalculation.client.gui.drawables.DLabel;
import me.towdium.jecalculation.client.gui.drawables.DLabelScroll;
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
        IntStream.range(0, 50).forEach(i -> labels.add(new LabelOreDict("plankWood", i)));


        add(new DLabelScroll(5, 5, 8, 3, DLabel.enumMode.RESULT).setLabels(labels));
        //add(new DTextField(7, 7, 60));
        //add(new DIcon(149, 7, 20, 20, Resource.ICN_HELP_N, Resource.ICN_HELP_F, "disambiguation.help"));
    }
}
