package me.towdium.jecalculation.network.packets;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PRecord {
    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_LAST = "last";
    RecordPlayer record;

    public PRecord(RecordPlayer record) {
        this.record = record;
    }

    public PRecord() {
    }

    public PRecord(FriendlyByteBuf buf) {
        CompoundTag tag = Objects.requireNonNull(buf.readNbt());
        LPlaceholder.state = false;
        record = new RecordPlayer(tag);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(record.serialize());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Controller.setRecordsServer(record));
    }
}
