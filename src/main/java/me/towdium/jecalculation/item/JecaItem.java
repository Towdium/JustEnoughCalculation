package me.towdium.jecalculation.item;

import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


/**
 * @author Towdium
 */
public class JecaItem extends Item {
    public static final String CRAFTING_NAME = "item_calculator_crafting";
    public static final String MATH_NAME = "item_calculator_math";

    public String getUnlocalizedName(int meta) {
        return getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName(stack.getItemDamage());
    }
}