package pers.towdium.just_enough_calculation.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.helpers.LocalizationHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Author: towdium
 * Date:   08/03/17.
 */
public class ItemLabel extends Item {
    public static final String keyName = "name";

    @Nullable
    public static String getName(ItemStack stack) {
        String ret = ItemStackHelper.NBT.getString(stack, false, keyName);
        return ret.isEmpty() ? null : ret;
    }

    public static ItemStack createStack(String name) {
        ItemStack ret = new ItemStack(JustEnoughCalculation.itemLabel);
        ItemStackHelper.NBT.setString(ret, false, keyName, name);
        return ret;
    }

    public static ItemStack setName(ItemStack stack, String name) {
        return ItemStackHelper.NBT.setString(stack, false, keyName, name);
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        String s = getName(stack);
        return s == null ? super.getItemStackDisplayName(stack) : LocalizationHelper.format("item.itemLabel.format", s).one;
    }
}
