package me.towdium.jecalculation;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.RecordPlayer;
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

/**
 * Author: Towdium
 * Date: 18-9-24
 */
@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JecaCapability {
    @CapabilityInject(Container.class)
    public static final Capability<Container> CAPABILITY_RECORD = null;

    public static RecordPlayer getRecord(EntityPlayer player) {
        //noinspection ConstantConditions
        return player.getCapability(JecaCapability.CAPABILITY_RECORD, EnumFacing.UP).getRecord();
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof EntityPlayerMP) {
            e.addCapability(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "record"),
                    new JecaCapability.Provider());
        }
    }

    @SubscribeEvent
    public static void onCloneCapability(PlayerEvent.Clone e) {
        RecordPlayer r = JecaCapability.getRecord(e.getOriginal());
        //noinspection ConstantConditions
        e.getEntityPlayer().getCapability(JecaCapability.CAPABILITY_RECORD, EnumFacing.UP).setRecord(r);
    }

    public static class Container {
        RecordPlayer record;

        public RecordPlayer getRecord() {
            if (record == null) record = new RecordPlayer();
            return record;
        }

        public void setRecord(RecordPlayer r) {
            record = r;
        }
    }

    public static class Storage implements Capability.IStorage<Container> {
        @Override
        public NBTBase writeNBT(Capability<Container> capability, Container instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<Container> capability, Container instance, EnumFacing side, NBTBase nbt) {
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {
        Container container;

        public Provider() {
            container = new Container();
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == JecaCapability.CAPABILITY_RECORD;
        }

        @Nullable
        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            //noinspection unchecked
            return capability == JecaCapability.CAPABILITY_RECORD ? (T) container : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return container.getRecord().serialize();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            boolean s = LPlaceholder.state;
            LPlaceholder.state = false;
            container.setRecord(new RecordPlayer(nbt));
            LPlaceholder.state = s;
        }
    }
}
