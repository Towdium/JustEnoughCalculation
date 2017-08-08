package me.towdium.jecalculation.item;

import me.towdium.jecalculation.util.helpers.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

/**
 * Author: Towdium
 * Date:   2016/7/19.
 */
public class ItemFluidContainer extends Item {
    public ItemFluidContainer() {
        setRegistryName("item_fluid_container");
        setUnlocalizedName("item_fluid_container");
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        Fluid f = ItemStackHelper.NBT.getFluid(stack);
        if (f == null) return super.getItemStackDisplayName(stack);
        long l = ItemStackHelper.NBT.getAmount(stack);
        return f.getLocalizedName(new FluidStack(f, (int) l));
    }
}
