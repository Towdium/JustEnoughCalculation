package pers.towdium.just_enough_calculation.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper;

import javax.annotation.Nonnull;

/**
 * Author: Towdium
 * Date:   2016/7/19.
 */
public class ItemFluidContainer extends Item {
    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        Fluid f = ItemStackHelper.NBT.getFluid(stack);
        if (f == null) return super.getItemStackDisplayName(stack);
        long l = ItemStackHelper.NBT.getAmount(stack);
        return f.getLocalizedName(new FluidStack(f, (int) l));
    }
}
