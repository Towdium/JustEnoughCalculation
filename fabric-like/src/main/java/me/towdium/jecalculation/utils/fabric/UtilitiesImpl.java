package me.towdium.jecalculation.utils.fabric;

import dev.architectury.fluid.FluidStack;
import me.towdium.jecalculation.compat.jei.JecaJEIPlugin;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import me.towdium.jecalculation.fabric_like.JecaConfig;
import me.towdium.jecalculation.fabric_like.JecaPlayerRecordAccessor;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.lang.reflect.Method;
import java.util.Optional;

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

    private static Method GET_FLUID;
    private static Method GET_TAG;

    //TODO: Use normal method to get value when possible
    @SuppressWarnings("unchecked")
    public static FluidStack createFluidStackFromJeiIngredient(Object object) {
        try {
            if (GET_FLUID == null)
                GET_FLUID = JecaJEIPlugin.FABRIC_FLUID_INGREDIENT_CLASS.getDeclaredMethod("getFluid");
            if (GET_TAG == null)
                GET_TAG = JecaJEIPlugin.FABRIC_FLUID_INGREDIENT_CLASS.getDeclaredMethod("getTag");
            if (object instanceof IJeiFluidIngredient fluid)
                return FluidStack.create((Fluid) GET_FLUID.invoke(fluid), fluid.getAmount(),
                        ((Optional<CompoundTag>) GET_TAG.invoke(fluid)).orElse(null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
