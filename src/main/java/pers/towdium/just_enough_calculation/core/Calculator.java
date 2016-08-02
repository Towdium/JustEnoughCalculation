package pers.towdium.just_enough_calculation.core;

import net.minecraft.item.ItemStack;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.ItemStackHelper.NBT;
import pers.towdium.just_enough_calculation.util.PlayerRecordHelper;
import pers.towdium.just_enough_calculation.util.wrappers.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Author: Towdium
 * Date:   2016/7/31.
 */

public class Calculator {
    List<CostList> costLists;

    public Calculator(ItemStack itemStack, long amount) {
        int count = 0;
        costLists = new ArrayList<>();
        costLists.add(new CostList(NBT.setData(itemStack.copy(), ItemStackHelper.EnumStackAmountType.NUMBER, amount)));
        List<ItemStack> cancellableItems = costLists.get(0).getValidItems();
        LOOP2:
        while (cancellableItems.size() != 0) {
            ++count;
            if (count > 10000) {
                throw new RuntimeException("JEC core circulates too many times for the operation. May be caused by number overflow or too complex recursion relations");
            }
            // all the items possible tp cancel
            for (ItemStack stack : cancellableItems) {
                // all the recipes for one item
                LOOP1:
                for (Recipe recipe : PlayerRecordHelper.getAllRecipeOutput(stack)) {
                    CostList record = new CostList(costLists.get(costLists.size() - 1), new CostList(recipe, NBT.setAmount(stack.copy(), -NBT.getAmount(stack)), getCount(stack, recipe)));
                    for (CostList costList : costLists) {
                        if (costList.equals(record)) {
                            continue LOOP1;
                        }
                        costLists.add(record);
                        cancellableItems = record.getValidItems();
                        continue LOOP2;
                    }
                }
            }
            break;
        }
    }

    public List<ItemStack> getInput() {
        return getList(itemStack -> NBT.getAmount(itemStack) < 0, costList -> costList.items, itemStack -> NBT.setAmount(itemStack, -NBT.getAmount(itemStack)));
    }

    public List<ItemStack> getOutput() {
        return getList(itemStack -> NBT.getAmount(itemStack) > 0, costList -> costList.items, itemStack -> itemStack);
    }

    public List<ItemStack> getCatalyst() {
        List<ItemStack> input = getInput();
        return getList(itemStack -> {
            Singleton<Boolean> flag = new Singleton<>(false);
            input.forEach(stack -> flag.value = flag.value || (ItemStackHelper.isItemEqual(itemStack, stack) && NBT.getAmountInternal(stack) >= NBT.getAmountInternal(stack)));
            return !flag.value && NBT.getAmount(itemStack) > 0;
        }, costList -> costList.catalyst, itemStack -> itemStack);
    }

    public List<ItemStack> getProcedure() {
        List<ItemStack> procedure = costLists.get(costLists.size() - 1).procedure;
        ListIterator<ItemStack> iterator = procedure.listIterator(procedure.size());
        List<ItemStack> buffer = new ArrayList<>();
        while (iterator.hasPrevious()) {
            CostList.merge(CostList.EnumMergeType.NORMAL_MERGE, buffer, iterator.previous());
        }
        return buffer;
    }

    List<ItemStack> getList(Predicate<ItemStack> filter, Function<CostList, List<ItemStack>> getter, Function<ItemStack, ItemStack> modifier) {
        List<ItemStack> buffer = new ArrayList<>();
        getter.apply(costLists.get(costLists.size() - 1)).stream().filter(filter).forEach(itemStack -> CostList.merge(CostList.EnumMergeType.NORMAL_MERGE, buffer, modifier.apply(itemStack.copy())));
        return buffer;
    }

    protected long getCount(ItemStack itemStack, Recipe recipe) {
        long a = recipe.getAmountOutput(itemStack);
        return (-ItemStackHelper.NBT.getAmountInternal(itemStack) + a - 1) / a;
    }
}

