package me.towdium.jecalculation.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackHelper {
    public static final Item EMPTY_ITEM = null;
    public static final ItemStack EMPTY_ITEM_STACK = new ItemStack((Item) null);

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack == EMPTY_ITEM_STACK || stack.getItem() == EMPTY_ITEM;
    }
}
