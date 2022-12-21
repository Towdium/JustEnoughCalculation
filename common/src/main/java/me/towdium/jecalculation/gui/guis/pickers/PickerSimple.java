package me.towdium.jecalculation.gui.guis.pickers;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.labels.LFluidStack;
import me.towdium.jecalculation.data.label.labels.LFluidTag;
import me.towdium.jecalculation.data.label.labels.LItemTag;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.gui.widgets.WIcon;
import me.towdium.jecalculation.gui.widgets.WLabelScroll;
import me.towdium.jecalculation.gui.widgets.WSearch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.towdium.jecalculation.gui.Resource.ICN_TEXT;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Environment(EnvType.CLIENT)
public class PickerSimple extends IPicker.Impl implements IGui {
    /**
     * @param labels label to be displayed for selection
     */
    public PickerSimple(List<ILabel> labels) {
        WLabelScroll ls = new WLabelScroll(7, 33, 8, 7, false).setLabels(labels)
                .setLsnrClick((i, v) -> notifyLsnr(i.get(v).getLabel()));
        add(new WIcon(7, 7, 20, 20, ICN_TEXT, "common.search"));
        add(new WSearch(26, 7, 90, ls));
        add(ls);
    }

    public static class FluidStack extends PickerSimple {
        public FluidStack() {
            super(BuiltInRegistries.FLUID.stream().filter(i -> i.isSource(i.defaultFluidState()))
                    .map(i -> new LFluidStack(1000, i)).collect(Collectors.toList()));
        }
    }

    public static class Tag extends PickerSimple {
        public Tag() {
            super(generate());
        }

        static List<ILabel> generate() {
            Stream<LItemTag> items = BuiltInRegistries.ITEM.getTags()
                    .filter(i -> i.getSecond().size() > 1)
                    .map(i -> new LItemTag(i.getFirst()))
                    .sorted(Comparator.comparing(LItemTag::getName));
            Stream<LFluidTag> fluids = BuiltInRegistries.FLUID.getTags()
                    .filter(i -> i.getSecond().size() > 1)
                    .map(i -> new LFluidTag(i.getFirst()))
                    .sorted(Comparator.comparing(LFluidTag::getName));
            return Stream.of(items, fluids).flatMap(i -> i).collect(Collectors.toList());
        }
    }
}
