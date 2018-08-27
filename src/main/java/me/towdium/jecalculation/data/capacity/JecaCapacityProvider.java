package me.towdium.jecalculation.data.capacity;

import me.towdium.jecalculation.data.structure.Recipes;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JecaCapacityProvider implements ICapabilityProvider, INBTSerializable<NBTTagList> {
    Recipes record;

    public JecaCapacityProvider(Recipes record) {
        this.record = record;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        //noinspection ConstantConditions
        return capability == JecaCapability.CAPABILITY_RECORD;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        //noinspection unchecked,ConstantConditions
        return capability == JecaCapability.CAPABILITY_RECORD ? (T) record : null;
    }

    @Override
    public NBTTagList serializeNBT() {
        return record.serialize();
    }

    @Override
    public void deserializeNBT(NBTTagList nbt) {
        record.deserialize(nbt);
    }
}
