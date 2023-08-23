package me.towdium.jecalculation.utils.fabric;

import dev.architectury.fluid.FluidStack;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import me.towdium.jecalculation.fabric_like.JecaConfig;
import me.towdium.jecalculation.fabric_like.JecaPlayerRecordAccessor;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class UtilitiesImpl {
    public static CompoundTag getCap(ItemStack itemStack) {
        return null;
    }

    public static ItemStack createItemStackWithCap(Item item, int count, CompoundTag cap) {
        return new ItemStack(item, count);
    }

    public static RecordPlayer getRecord(Player player) {
        return player instanceof JecaPlayerRecordAccessor accessor ? accessor.Jeca_getRecord() : null;
    }

    public static boolean isClientMode() {
        return JecaConfig.clientMode;
    }

    public static boolean areCapsCompatible(ItemStack itemStack, ItemStack itemStack1) {
        return true;
    }

    public static FluidStack createFluidStackFromJeiIngredient(Object object) {
        if (object instanceof IJeiFluidIngredient fluid)
            return FluidStack.create(fluid.getFluid(), fluid.getAmount(), fluid.getTag().orElse(null));
        return null;
    }
}
