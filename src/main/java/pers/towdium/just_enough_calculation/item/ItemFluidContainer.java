package pers.towdium.just_enough_calculation.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.LocalizationHelper;

import java.util.List;

/**
 * Author: Towdium
 * Date:   2016/7/19.
 */
public class ItemFluidContainer extends Item {
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        tooltip.add(LocalizationHelper.format("item.itemFluidContainer.tooltip", ItemStackHelper.NBT.getAmount(stack)));
        super.addInformation(stack, playerIn, tooltip, advanced);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Fluid f = ItemStackHelper.NBT.getFluid(stack);
        if (f == null) return super.getItemStackDisplayName(stack);
        long l = ItemStackHelper.NBT.getAmount(stack);
        return f.getLocalizedName(new FluidStack(f, (int) l));
    }
}
