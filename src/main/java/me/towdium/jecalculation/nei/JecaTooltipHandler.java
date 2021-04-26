package me.towdium.jecalculation.nei;

import codechicken.nei.guihook.IContainerTooltipHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class JecaTooltipHandler implements IContainerTooltipHandler {
    @Override
    public List<String> handleTooltip(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
        return currenttip;
    }

    @Override
    public List<String> handleItemDisplayName(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
//        if (gui instanceof JecaGuiContainer) {
//            ((JecaGuiContainer)gui).handleMouseOverNEIItemPanel(itemstack);
//        }
        return currenttip;
    }

    @Override
    public List<String> handleItemTooltip(GuiContainer gui,
                                          ItemStack itemstack,
                                          int mousex,
                                          int mousey,
                                          List<String> currenttip) {
//        if (gui instanceof JecaGuiContainer) {
//            ((JecaGuiContainer)gui).handleMouseOverNEIItemPanel(itemstack);
//        }
        return currenttip;
    }
}
