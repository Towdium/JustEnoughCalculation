package me.towdium.jecalculation.core;

import net.minecraft.item.ItemStack;

public class ItemStackHelper {
    public static boolean isTypeEqual(ItemStack one, ItemStack two) {
        return one != null && two != null && one.getItem() == two.getItem() && getMetadata(one) == getMetadata(two);
    }

    private static int getMetadata(ItemStack stack) {
        return stack.getItemDamage();
    }
}
