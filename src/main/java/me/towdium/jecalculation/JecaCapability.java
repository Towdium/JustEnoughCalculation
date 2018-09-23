package me.towdium.jecalculation;

import me.towdium.jecalculation.data.structure.Recipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Author: Towdium
 * Date: 18-9-24
 */
public class JecaCapability {
    @CapabilityInject(Recipes.class)
    public static final Capability<Recipes> CAPABILITY_RECORD = null;

    public static Recipes getRecipes(EntityPlayer player) {
        //noinspection ConstantConditions
        return player.getCapability(JecaCapability.CAPABILITY_RECORD, EnumFacing.UP);
    }

    public static class Storage implements Capability.IStorage<Recipes> {
        @Override
        public NBTBase writeNBT(Capability<Recipes> capability, Recipes instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<Recipes> capability, Recipes instance, EnumFacing side, NBTBase nbt) {
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<NBTTagList> {
        Recipes record;

        public Provider(Recipes record) {
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
}
