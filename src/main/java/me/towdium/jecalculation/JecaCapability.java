package me.towdium.jecalculation;

import me.towdium.jecalculation.data.structure.Recipes;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

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

    public static class Provider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {
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
        public NBTTagCompound serializeNBT() {
            return record.serialize();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            if (nbt.isEmpty()) {
                File file = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/default.json");
                nbt = Utilities.Json.read(file);
                if (nbt != null) record.deserialize(nbt);
            } else record.deserialize(nbt);
        }
    }
}
