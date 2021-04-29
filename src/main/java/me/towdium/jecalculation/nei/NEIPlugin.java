package me.towdium.jecalculation.nei;

import codechicken.nei.guihook.GuiContainerManager;
import me.towdium.jecalculation.core.label.ILabel;
import net.minecraft.item.ItemStack;

public class NEIPlugin {
    public static final ILabel.RegistryConverterItem registryItem = ILabel.RegistryConverterItem.INSTANCE;
    public static final ILabel.RegistryConverterFluid registryFluid = ILabel.RegistryConverterFluid.INSTANCE;

    public static void init() {
        GuiContainerManager.addTooltipHandler(new JecaTooltipHandler());
    }

    private static ItemStack currentItemStack;

    public static ILabel getLabelUnderMouse() {
        if (NEIPlugin.currentItemStack == null) return ILabel.EMPTY;
        else return registryItem.toLabel(NEIPlugin.currentItemStack);
    }

    public static void setLabelUnderMouse(ItemStack itemStack) {
        NEIPlugin.currentItemStack = itemStack;
    }
}
