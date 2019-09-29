package me.towdium.jecalculation.network.packets;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public PRecord(PacketBuffer buf) {
        CompoundNBT tag = Objects.requireNonNull(buf.readCompoundTag());
        LPlaceholder.state = false;
        record = new RecordPlayer(tag);
    }

    public void write(PacketBuffer buf) {
        buf.writeCompoundTag(record.serialize());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Controller.setRecordsServer(record));
    }
}
