package me.towdium.jecalculation.client.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.drawables.WLabel;
import me.towdium.jecalculation.client.gui.drawables.WLabelScroll;
import me.towdium.jecalculation.client.gui.drawables.WSearch;
import me.towdium.jecalculation.client.gui.drawables.WTextField;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.core.labels.labels.LabelFluidStack;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GuiEditorFluidStack extends ILabel.RegistryEditor.Editor {
    public GuiEditorFluidStack() {
        WLabelScroll ls = new WLabelScroll(7, 33, 8, 7, WLabel.enumMode.PICKER, true).setLabels(
                FluidRegistry.getRegisteredFluids().entrySet().stream()
                        .map(e -> new LabelFluidStack(e.getValue(), 1000)).collect(Collectors.toList()));
        WTextField tf = new WTextField(25, 7, 90);
        add(new WSearch(l -> callback.value.accept(l), tf, ls));
    }
}
