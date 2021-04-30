package me.towdium.jecalculation.nei;

import codechicken.nei.guihook.GuiContainerManager;
import me.towdium.jecalculation.data.label.ILabel;
import net.minecraft.item.ItemStack;

public class NEIPlugin {

    public static void init() {
        GuiContainerManager.addTooltipHandler(new JecaTooltipHandler());
    }

    private static ItemStack currentItemStack;

    public static ILabel getLabelUnderMouse() {
        if (NEIPlugin.currentItemStack == null) return ILabel.EMPTY;
        else return ILabel.CONVERTER_ITEM.toLabel(NEIPlugin.currentItemStack);
        // else if (o instanceof FluidStack) return ILabel.CONVERTER_FLUID.toLabel(((FluidStack) o));
    }

    public static void setLabelUnderMouse(ItemStack itemStack) {
        NEIPlugin.currentItemStack = itemStack;
    }
}
