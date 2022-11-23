package me.towdium.jecalculation.utils.forge;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import me.towdium.jecalculation.forge.JecaCapability;
import me.towdium.jecalculation.forge.JecaConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class UtilitiesImpl {
    public static CompoundTag getCap(ItemStack itemStack) {
        CompoundTag nbt = itemStack.serializeNBT();
        return nbt.contains("ForgeCaps") ? nbt.getCompound("ForgeCaps") : null;
    }

    public static ItemStack createItemStackWithCap(Item item, int count, CompoundTag cap) {
        return new ItemStack(item, count, cap);
    }

    public static RecordPlayer getRecord(Player player) {
        return JecaCapability.getRecord(player);
    }

    public static boolean isClientMode() {
        return JecaConfig.clientMode.get();
    }

    public static boolean areCapsCompatible(ItemStack itemStack, ItemStack itemStack1) {
        return itemStack.areCapsCompatible(itemStack1);
    }

    public static FluidStack createFluidStackFromJeiIngredient(Object object) {
        if(object instanceof net.minecraftforge.fluids.FluidStack fluidStack)
            return FluidStackHooksForge.fromForge(fluidStack);
        return null;
    }
}
