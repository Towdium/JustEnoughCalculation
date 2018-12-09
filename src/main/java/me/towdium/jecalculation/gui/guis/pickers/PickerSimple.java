package me.towdium.jecalculation.gui.guis.pickers;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.labels.LFluidStack;
import me.towdium.jecalculation.data.label.labels.LOreDict;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.gui.widgets.WIcon;
import me.towdium.jecalculation.gui.widgets.WLabel;
import me.towdium.jecalculation.gui.widgets.WLabelScroll;
import me.towdium.jecalculation.gui.widgets.WSearch;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class PickerSimple extends IPicker.Impl implements IGui {
    /**
     * @param labels  label to be displayed for selection
     * @param l18nKey localization key for help string,
     *                entire key should be "gui.l18nKey.help.tooltip"
     */
    public PickerSimple(List<ILabel> labels, String l18nKey) {
        WLabelScroll ls = new WLabelScroll(7, 33, 8, 7, WLabel.enumMode.PICKER, true)
                .setLabels(labels).setLsnrUpdate(callback);
        add(new WSearch(26, 7, 90, ls));
        add(new WIcon(7, 7, 20, 20, Resource.ICN_TEXT, l18nKey + ".text"));
        add(ls);
    }

    public static class FluidStack extends PickerSimple {
        public FluidStack() {
            super(FluidRegistry.getRegisteredFluids().entrySet().stream()
                            .map(e -> new LFluidStack(1000, e.getValue())).collect(Collectors.toList()),
                    "picker_fluid_stack");
        }
    }

    public static class OreDict extends PickerSimple {
        public OreDict() {
            super(generate(), "picker_ore_dict");
        }

        static List<ILabel> generate() {
            List<ILabel> present = new ArrayList<>();
            List<ILabel> empty = new ArrayList<>();
            Arrays.stream(OreDictionary.getOreNames()).map(LOreDict::new).forEach(i -> (i.isEmpty() ? empty : present).add(i));
            present.addAll(empty);
            return present;
        }
    }
}
