package pers.towdium.just_enough_calculation.core;

import net.minecraft.item.ItemStack;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.ItemStackHelper.NBT;
import pers.towdium.just_enough_calculation.util.ItemStackHelper.EnumStackAmountType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;


/**
 * Author: Towdium
 * Date:   2016/6/27.
 */
public class CostList {
    List<ItemStack> items;
    List<ItemStack> catalyst;

    public CostList(ItemStack itemStack) {
        items = new ArrayList<>();
        catalyst = new LinkedList<>();
        catalyst.add(itemStack.copy());
        items.add(itemStack.copy());
    }

    public CostList(Recipe recipe) {
        items = new ArrayList<>();
        catalyst = new LinkedList<>();
        merge(true, false, items, recipe.output);
        merge(true, false, catalyst, recipe.catalyst);
        merge(true, false, catalyst, recipe.input);
        merge(false, false, items, recipe.input);
    }

    public CostList(CostList caller, CostList callee) {
        items = new ArrayList<>();
        catalyst = new LinkedList<>();
        merge(true, false, items, caller.items);
        merge(true, false, catalyst, caller.catalyst);
        merge(true, false, items, callee.items);
        merge(true, false, catalyst, callee.catalyst);
        merge(false, true, catalyst, callee.items);
    }

    public static void merge(boolean add, boolean positiveOnly, List<ItemStack> container, List<ItemStack> itemStacks) {
        itemStacks.forEach(itemStack -> merge(add, positiveOnly, container, itemStack));
    }

    public static void merge(boolean add, boolean positiveOnly, List<ItemStack> container, ItemStack[] itemStacks) {
        for (ItemStack itemStackExternal : itemStacks) {
            merge(add, positiveOnly, container, itemStackExternal);
        }
    }

    static void merge(boolean add, boolean positiveOnly, List<ItemStack> container, ItemStack itemStack) {
        for (ItemStack itemStackInternal : container) {
            if (merge(itemStackInternal, itemStack, positiveOnly ? ((i, j) -> j > i ? 0 : j > 0 ? i - j : i) : add ? ((i, j) -> i + j) : ((i, j) -> i - j)))
                return;
        }
        ItemStack stack = itemStack.copy();
        if (!add) {
            NBT.setAmount(stack, -NBT.getAmount(stack));
        }
        container.add(stack);
    }

    static boolean merge(ItemStack stackInternal, ItemStack stackExternal, BiFunction<Long, Long, Long> func) {
        if (ItemStackHelper.isItemEqual(stackExternal, stackInternal)) {
            if (NBT.getType(stackExternal) == NBT.getType(stackInternal)) {
                NBT.setData(stackInternal, NBT.getType(stackInternal), func.apply(NBT.getAmount(stackExternal), NBT.getAmount(stackInternal)));
                return true;
            } else if (NBT.getType(stackExternal) == EnumStackAmountType.FLUID || NBT.getType(stackInternal) == EnumStackAmountType.FLUID) {
                return false;
            } else {
                NBT.setData(stackInternal, EnumStackAmountType.PERCENTAGE, func.apply(NBT.getAmountInternal(stackExternal), NBT.getAmountInternal(stackInternal)));
                return true;
            }
        } else {
            return false;
        }
    }
}
