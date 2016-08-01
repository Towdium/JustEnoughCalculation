package pers.towdium.just_enough_calculation.core;

import net.minecraft.item.ItemStack;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.PlayerRecordHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Towdium
 * Date:   2016/7/31.
 */

public class Calculator {
    List<CostList> costLists;

    public Calculator(ItemStack itemStack, long amount) {
        costLists = new ArrayList<>();
        costLists.add(new CostList(ItemStackHelper.NBT.setData(itemStack.copy(), ItemStackHelper.EnumStackAmountType.NUMBER, amount)));
        List<ItemStack> cancellableItems = costLists.get(0).getValidItems();
        LOOP2:
        while (cancellableItems.size() != 0) {
            // all the items possible tp cancel
            for (ItemStack stack : cancellableItems) {
                // all the recipes for one item
                LOOP1:
                for (Recipe recipe : PlayerRecordHelper.getAllRecipeOutput(stack)) {
                    CostList record = new CostList(costLists.get(costLists.size() - 1), new CostList(recipe, getCount(stack, recipe)));
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

    public CostList getCost() {
        return costLists.get(costLists.size() - 1);
    }

    protected long getCount(ItemStack itemStack, Recipe recipe) {
        long a = recipe.getAmountOutput(itemStack);
        return (long) Math.ceil(ItemStackHelper.NBT.getAmountInternal(itemStack) / (double) -a);
    }
}

