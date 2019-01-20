package me.towdium.jecalculation.network.packets;

import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;

public class PRecord implements IMessage {
    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_LAST = "last";
    RecordPlayer record;

    public PRecord(RecordPlayer record) {
        this.record = record;
    }

    public PRecord() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = Objects.requireNonNull(ByteBufUtils.readTag(buf));
        record = new RecordPlayer(tag);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, record.serialize());
    }

    public static class Handler implements IMessageHandler<PRecord, IMessage> {
        @Override
        public IMessage onMessage(PRecord message, MessageContext ctx) {
            Controller.setRecordsServer(message.record);
            return null;
        }
    }
}
