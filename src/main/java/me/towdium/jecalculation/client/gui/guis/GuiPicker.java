package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.IWPicker;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.core.labels.labels.LabelFluidStack;
import me.towdium.jecalculation.core.labels.labels.LabelOreDict;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
public class GuiPicker extends IWPicker.Simple {
    /**
     * @param labels  labels to be displayed for selection
     * @param l18nKey localization key for help string,
     *                entire key should be "gui.l18nKey.help.tooltip"
     */
    public GuiPicker(List<ILabel> labels, String l18nKey) {
        WLabelScroll ls = new WLabelScroll(7, 33, 8, 7, WLabel.enumMode.PICKER, true).setLabels(labels);
        WTextField tf = new WTextField(25, 7, 90);
        add(new WSearch(l -> callback.value.accept(l), tf, ls));
        add(new WIcon(149, 7, 20, 20, Resource.ICN_HELP_N, Resource.ICN_HELP_F, l18nKey + ".help"));
    }

    public static class PLabelFluidStack extends GuiPicker {
        public PLabelFluidStack() {
            super(FluidRegistry.getRegisteredFluids().entrySet().stream()
                            .map(e -> new LabelFluidStack(e.getValue(), 1000)).collect(Collectors.toList()),
                    "picker_label_fluid_stack");
        }
    }

    public static class PLabelOreDict extends GuiPicker {
        public PLabelOreDict() {
            super(Arrays.stream(OreDictionary.getOreNames()).map(LabelOreDict::new).collect(Collectors.toList()),
                    "picker_l_fluid_stack");
        }
    }
}
