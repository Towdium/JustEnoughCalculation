package me.towdium.jecalculation.core;

import me.towdium.jecalculation.util.exception.IllegalPositionException;
import me.towdium.jecalculation.util.helpers.ItemStackHelper;
import me.towdium.jecalculation.util.helpers.PlayerRecordHelper;
import me.towdium.jecalculation.util.wrappers.Pair;
import me.towdium.jecalculation.util.wrappers.Singleton;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Author: Towdium
 * Date:   2016/7/31.
 */

public class Calculator {
    List<CostList> costLists;

    /**
     * Create Calculator from a normal ItemStack with given amount.
     * This method will convert the stack to JECItemStack with original stack unchanged.
     *
     * @param itemStack the stack
     * @param amount    the amount
     */
    public Calculator(ItemStack itemStack, long amount) {
        this(new CostList(ItemStackHelper.NBT.setData(itemStack.copy(), ItemStackHelper.EnumStackAmountType.NUMBER, amount)));
    }

    /**
     * Create Calculator for a normal ItemStack for inventory check
     * This method will convert the stack to JECItemStack with original stack unchanged.
     *
     * @param itemStacks inventory stacks
     * @param stack      destination
     * @param amount     the amount
     */
    public Calculator(List<ItemStack> itemStacks, ItemStack stack, long amount) {
        this(new CostList(new CostList(ItemStackHelper.NBT.setData(stack.copy(), ItemStackHelper.EnumStackAmountType.NUMBER, amount)), new CostList(itemStacks)));
        costLists.add(new CostList(costLists.get(costLists.size() - 1), new CostList(itemStacks), true));
    }

    public Calculator(CostList cost) {
        int count = 0;
        costLists = new ArrayList<>();
        costLists.add(cost);
        List<ItemStack> cancellableItems = costLists.get(0).getValidItems();
        LOOP2:
        while (cancellableItems.size() != 0) {
            ++count;
            if (count > 2000) {
                throw new JECCalculatingCoreException();
            }
            // all the items possible tp cancel
            for (ItemStack stack : cancellableItems) {
                // all the recipes for one item
                LOOP1:
                for (Recipe recipe : PlayerRecordHelper.getAllRecipeOutput(stack)) {
                    CostList record = new CostList(costLists.get(costLists.size() - 1), new CostList(recipe, ItemStackHelper.NBT.setAmount(stack.copy(), -ItemStackHelper.NBT.getAmount(stack)), getCount(stack, recipe)));
                    for (CostList costList : costLists) {
                        if (record.includesAll(costList)) {
                            continue LOOP1;
                        }
                    }
                    costLists.add(record);
                    cancellableItems = record.getValidItems();
                    continue LOOP2;
                }
            }
            break;
        }
        costLists.get(costLists.size() - 1).finalise();
    }

    public List<ItemStack> getInput() {
        return getList(itemStack -> ItemStackHelper.NBT.getAmount(itemStack) < 0, costList -> costList.items, itemStack -> ItemStackHelper.NBT.setAmount(itemStack, -ItemStackHelper.NBT.getAmount(itemStack)));
    }

    public List<ItemStack> getOutput() {
        return getList(itemStack -> ItemStackHelper.NBT.getAmount(itemStack) > 0, costList -> costList.items, itemStack -> itemStack);
    }

    public List<ItemStack> getCatalyst() {
        return getList(itemStack -> ItemStackHelper.NBT.getAmount(itemStack) > 0, costList -> costList.catalyst, itemStack -> itemStack);
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
        long a = recipe.getAmountOutputInternal(itemStack);
        return (-ItemStackHelper.NBT.getAmountInternal(itemStack) + a - 1) / a;
    }

    static class CostList {
        static final BiFunction<Long, Long, Long> NORMAL_MERGE = (i, j) -> i + j;
        static final BiFunction<Long, Long, Long> NORMAL_CANCEL = (i, j) -> i - j;
        static final BiFunction<Long, Long, Long> CATALYST_CANCEL = (i, j) -> j > i ? (i > 0 ? 0 : i) : j > 0 ? i - j : i;
        static final BiFunction<Long, Long, Long> CATALYST_MERGE = (i, j) -> -j > i ? 0 : j < 0 ? i + j : i;
        List<ItemStack> items = new ArrayList<>();
        List<ItemStack> catalyst = new ArrayList<>();
        List<ItemStack> procedure = new ArrayList<>();

        public CostList(List<ItemStack> inventory) {
            inventory.stream().filter(is -> !is.isEmpty()).forEach(stack -> items.add(ItemStackHelper.toItemStackJEC(stack.copy())));
        }

        public CostList(ItemStack itemStack) {
            long amount = ItemStackHelper.NBT.getAmount(itemStack);
            catalyst.add(itemStack.copy());
            items.add(ItemStackHelper.NBT.setAmount(itemStack.copy(), -amount));
        }

        public CostList(Recipe recipe, ItemStack dest, long amount) {
            merge(EnumMergeType.NORMAL_MERGE, items, recipe.output);
            merge(EnumMergeType.NORMAL_MERGE, catalyst, recipe.input);
            catalyst.forEach(itemStack -> ItemStackHelper.NBT.setAmount(itemStack, ItemStackHelper.NBT.getAmount(itemStack) * amount));
            merge(EnumMergeType.NORMAL_MERGE, catalyst, recipe.catalyst);
            merge(EnumMergeType.NORMAL_CANCEL, items, recipe.input);
            items.forEach(itemStack -> ItemStackHelper.NBT.setAmount(itemStack, ItemStackHelper.NBT.getAmount(itemStack) * amount));
            Pair<Long, ItemStackHelper.EnumStackAmountType> p = recipe.getAmountOutput(dest);
            procedure.add(ItemStackHelper.NBT.setData(dest.copy(), p.two, p.one * amount));
        }

        public CostList(CostList caller, CostList callee) {
            this(caller, callee, false);
        }

        /**
         * Calculate to get a new CostList
         *
         * @param caller          the original CostList
         * @param callee          the CostList to be merged
         * @param inventoryCancel whether it is used for inventory scan
         */
        public CostList(CostList caller, CostList callee, boolean inventoryCancel) {
            if (inventoryCancel) {
                merge(EnumMergeType.NORMAL_MERGE, items, caller.items);
                merge(EnumMergeType.NORMAL_MERGE, catalyst, caller.catalyst);
                // since the callee is generated by inventory stacks
                // so the calculation should be inverted
                merge(EnumMergeType.CATALYST_CANCEL, items, callee.items);
                merge(EnumMergeType.CATALYST_CANCEL, catalyst, callee.items);
                caller.procedure.forEach(itemStack -> procedure.add(itemStack.copy()));
            } else {
                merge(EnumMergeType.NORMAL_MERGE, items, caller.items);
                merge(EnumMergeType.NORMAL_MERGE, catalyst, caller.catalyst);
                merge(EnumMergeType.NORMAL_MERGE, items, callee.items);
                merge(EnumMergeType.CATALYST_CANCEL, catalyst, callee.catalyst);
                merge(EnumMergeType.NORMAL_MERGE, catalyst, callee.catalyst);
                merge(EnumMergeType.CATALYST_CANCEL, catalyst, callee.items);
                caller.procedure.forEach(itemStack -> procedure.add(itemStack.copy()));
                callee.procedure.forEach(itemStack -> procedure.add(itemStack.copy()));
            }
        }

        public static void merge(EnumMergeType type, List<ItemStack> container, List<ItemStack> itemStacks) {
            itemStacks.forEach(itemStack -> merge(type, container, itemStack));
        }

        public static void merge(EnumMergeType type, List<ItemStack> container, ItemStack[] itemStacks) {
            for (ItemStack itemStack : itemStacks) {
                merge(type, container, itemStack);
            }
        }

        public static void merge(EnumMergeType type, List<ItemStack> container, ItemStack itemStack) {
            if (itemStack.isEmpty()) {
                return;
            }
            for (ItemStack itemStackInternal : container) {
                if (merge(itemStackInternal, itemStack, type.getFunc()))
                    return;
            }
            ItemStack stack = itemStack.copy();
            ItemStackHelper.NBT.setAmount(stack, type.getFunc().apply(0L, ItemStackHelper.NBT.getAmount(stack)));
            container.add(stack);
        }

        static boolean merge(ItemStack stackInternal, ItemStack stackExternal, BiFunction<Long, Long, Long> func) {
            if (ItemStackHelper.isItemEqual(stackExternal, stackInternal)) {
                if (ItemStackHelper.NBT.getType(stackExternal) == ItemStackHelper.NBT.getType(stackInternal)) {
                    ItemStackHelper.NBT.setData(stackInternal, ItemStackHelper.NBT.getType(stackInternal), func.apply(ItemStackHelper.NBT.getAmount(stackInternal), ItemStackHelper.NBT.getAmount(stackExternal)));
                    return true;
                } else if (ItemStackHelper.NBT.getType(stackExternal) == ItemStackHelper.EnumStackAmountType.FLUID || ItemStackHelper.NBT.getType(stackInternal) == ItemStackHelper.EnumStackAmountType.FLUID) {
                    return false;
                } else {
                    ItemStackHelper.NBT.setData(stackInternal, ItemStackHelper.EnumStackAmountType.PERCENTAGE, func.apply(ItemStackHelper.NBT.getAmountInternal(stackInternal), ItemStackHelper.NBT.getAmountInternal(stackExternal)));
                    return true;
                }
            } else {
                return false;
            }
        }

        public boolean includesAll(CostList old) {
            BiPredicate<ItemStack, List<ItemStack>> stackChecker = (itemStack, stacks) -> {
                Singleton<Boolean> flag = new Singleton<>(null);
                stacks.stream().filter(s -> ItemStackHelper.isItemEqual(s, itemStack))
                        .forEach(s -> flag.value = ItemStackHelper.NBT.getAmountInternal(itemStack) > ItemStackHelper.NBT.getAmountInternal(s));
                return flag.value == null ? ItemStackHelper.NBT.getAmount(itemStack) > 0 : flag.value;
            };

            BiPredicate<List<ItemStack>, List<ItemStack>> listChecker = (stacks1, stacks2) -> {
                Singleton<Boolean> flag = new Singleton<>(false);
                stacks1.forEach(itemStack -> flag.value = flag.value || stackChecker.test(itemStack, stacks2));
                return flag.value;
            };

            return !listChecker.test(items, old.items);
        }

        public void finalise() {
            merge(EnumMergeType.CATALYST_MERGE, catalyst, items);
        }

        public List<ItemStack> getValidItems() {
            List<ItemStack> buffer = new ArrayList<>();
            items.stream().filter(itemStack -> ItemStackHelper.NBT.getAmount(itemStack) < 0).forEach(buffer::add);
            return buffer;
        }

        enum EnumMergeType {
            NORMAL_MERGE, NORMAL_CANCEL, CATALYST_CANCEL, CATALYST_MERGE;

            BiFunction<Long, Long, Long> getFunc() {
                switch (this) {
                    case NORMAL_MERGE:
                        return CostList.NORMAL_MERGE;
                    case NORMAL_CANCEL:
                        return CostList.NORMAL_CANCEL;
                    case CATALYST_CANCEL:
                        return CostList.CATALYST_CANCEL;
                    case CATALYST_MERGE:
                        return CostList.CATALYST_MERGE;
                    default:
                        throw new IllegalPositionException();
                }
            }
        }
    }

    public static class JECCalculatingCoreException extends RuntimeException {
    }
}

