package me.towdium.jecalculation;

import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
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
    public static final Capability<Container> CAPABILITY_RECORD = CapabilityManager.get(new CapabilityToken<>(){});



    public static RecordPlayer getRecord(Player player) {
        //noinspection ConstantConditions
        return player.getCapability(JecaCapability.CAPABILITY_RECORD, Direction.UP).orElseGet(Container::new).getRecord();
    }


    @SubscribeEvent
    public static void onRegisterCapability(RegisterCapabilitiesEvent event) {
        event.register(Container.class);
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof ServerPlayer) {
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

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        Container container;

        public Provider() {
            container = new Container();
        }


        @Override
        public CompoundTag serializeNBT() {
            return container.getRecord().serialize();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
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
