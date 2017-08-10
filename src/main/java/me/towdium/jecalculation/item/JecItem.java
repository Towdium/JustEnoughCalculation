package me.towdium.jecalculation.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JecItem extends Item {
    public String getUnlocalizedName(int meta) {
        return getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName(stack.getMetadata());
    }
}
