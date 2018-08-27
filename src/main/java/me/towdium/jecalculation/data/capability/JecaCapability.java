package me.towdium.jecalculation.data.capability;

import me.towdium.jecalculation.data.structure.Recipes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class JecaCapability implements Capability.IStorage<Recipes> {
    @CapabilityInject(Recipes.class)
    public static final Capability<Recipes> CAPABILITY_RECORD = null;

    @Override
    public NBTBase writeNBT(Capability<Recipes> capability, Recipes instance, EnumFacing side) {
        return null;
    }

    @Override
    public void readNBT(Capability<Recipes> capability, Recipes instance, EnumFacing side, NBTBase nbt) {
    }
}