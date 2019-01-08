package me.towdium.jecalculation;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.structure.Recipes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

/**
 * Author: Towdium
 * Date: 18-9-24
 */
@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JecaCapability {
    @CapabilityInject(Recipes.class)
    public static final Capability<Container> CAPABILITY_RECORD = null;

    public static Recipes getRecipes(EntityPlayer player) {
        //noinspection ConstantConditions
        Container c = player.getCapability(JecaCapability.CAPABILITY_RECORD, EnumFacing.UP);
        Objects.requireNonNull(c);
        return c.get();
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof EntityPlayerMP) {
            e.addCapability(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "recipes"),
                    new JecaCapability.Provider());
        }
    }

    @SubscribeEvent
    public static void onCloneCapability(PlayerEvent.Clone e) {
        Recipes r = JecaCapability.getRecipes(e.getOriginal());
        //noinspection ConstantConditions
        e.getEntityPlayer().getCapability(JecaCapability.CAPABILITY_RECORD, EnumFacing.UP).set(r);
    }

    public static class Container {
        Recipes recipes;

        public Recipes get() {
            if (recipes == null) recipes = new Recipes();
            return recipes;
        }

        public void set(Recipes r) {
            recipes = r;
        }
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
        Container container;

        public Provider() {
            container = new Container();
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            //noinspection ConstantConditions
            return capability == JecaCapability.CAPABILITY_RECORD;
        }

        @Nullable
        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            //noinspection unchecked,ConstantConditions
            return capability == JecaCapability.CAPABILITY_RECORD ? (T) container : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            if (container.recipes == null) container.recipes = new Recipes();
            return container.recipes.serialize();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            container.recipes = new Recipes(nbt);
        }
    }
}
