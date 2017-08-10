package me.towdium.jecalculation.item.items;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.item.JecItem;
import me.towdium.jecalculation.utils.IllegalPositionException;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemCalculator extends JecItem {
    public ItemCalculator() {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.TOOLS);
        setRegistryName("item_calculator");
        setUnlocalizedName("item_calculator");
    }

    @Override
    public String getUnlocalizedName(int meta) {
        switch (meta) {
            case 0:
                return "item.item_calculator_crafting";
            case 1:
                return "item.item_calculator_math";
            default:
                throw new IllegalPositionException();
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == CreativeTabs.SEARCH || tab == CreativeTabs.TOOLS) {
            items.add(new ItemStack(this, 1, 0));
            items.add(new ItemStack(this, 1, 1));
        }
    }
}
