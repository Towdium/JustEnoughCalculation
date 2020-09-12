package me.towdium.jecalculation;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
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

    public static RecordPlayer getRecord(PlayerEntity player) {
        //noinspection ConstantConditions
        return player.getCapability(JecaCapability.CAPABILITY_RECORD, Direction.UP).orElseGet(Container::new).getRecord();
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof ServerPlayerEntity) {
            e.addCapability(new ResourceLocation(JustEnoughCalculation.MODID, "record"),
                    new JecaCapability.Provider());
        }
    }

    @SubscribeEvent
    public static void onCloneCapability(PlayerEvent.Clone e) {
        RecordPlayer r = JecaCapability.getRecord(e.getOriginal());
        //noinspection ConstantConditions
        e.getPlayer().getCapability(JecaCapability.CAPABILITY_RECORD, Direction.UP).orElseGet(Container::new).setRecord(r);
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
        @Nullable
        @Override
        public INBT writeNBT(Capability<Container> capability, Container instance, Direction side) {
            return null;
        }

        @Override
        public void readNBT(Capability<Container> capability, Container instance, Direction side, INBT nbt) {

        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
        Container container;

        public Provider() {
            container = new Container();
        }


        @Override
        public CompoundNBT serializeNBT() {
            return container.getRecord().serialize();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            boolean s = LPlaceholder.state;
            LPlaceholder.state = false;
            container.setRecord(new RecordPlayer(nbt));
            LPlaceholder.state = s;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return getCapability(cap);
        }

        @Nonnull
        @Override
        @SuppressWarnings({"unchecked", "ConstantConditions"})
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
            return LazyOptional.of(cap == JecaCapability.CAPABILITY_RECORD ? () -> (T) container : null);
        }
    }
}
