package me.towdium.jecalculation.utils;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.polyfill.NBTHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemStackHelper {
    public static final Item EMPTY_ITEM = null;
    public static final ItemStack EMPTY_ITEM_STACK = new ItemStack((Item)null);

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack == EMPTY_ITEM_STACK || stack.getItem() == EMPTY_ITEM;
    }

}
