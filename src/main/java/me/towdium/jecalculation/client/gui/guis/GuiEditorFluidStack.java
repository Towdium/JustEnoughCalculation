package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.drawables.DLabel;
import me.towdium.jecalculation.client.gui.drawables.DLabelScroll;
import me.towdium.jecalculation.client.gui.drawables.DSearch;
import me.towdium.jecalculation.client.gui.drawables.DTextField;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.core.labels.labels.LabelFluidStack;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
public class GuiEditorFluidStack extends ILabel.RegistryEditor.Editor {
    public GuiEditorFluidStack() {
        DLabelScroll ls = new DLabelScroll(7, 33, 8, 7, DLabel.enumMode.PICKER, true).setLabels(
                FluidRegistry.getRegisteredFluids().entrySet().stream()
                        .map(e -> new LabelFluidStack(e.getValue(), 1000)).collect(Collectors.toList()));
        DTextField tf = new DTextField(25, 7, 90);
        add(new DSearch(l -> callback.value.accept(l), tf, ls));
    }
}
