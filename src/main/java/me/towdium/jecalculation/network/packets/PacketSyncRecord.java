package me.towdium.jecalculation.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.network.PlayerHandlerClient;
import me.towdium.jecalculation.network.PlayerHandlerServer;
import me.towdium.jecalculation.network.ProxyClient;
import me.towdium.jecalculation.network.ProxyServer;

import java.util.UUID;

/**
 * @author Towdium
 */
public class PacketSyncRecord implements IMessage, IMessageHandler<PacketSyncRecord, IMessage> {
    PlayerHandlerClient client;

    public PacketSyncRecord(){}

    public PacketSyncRecord(PlayerHandlerClient client){
        this.client = client;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (client == null){
            buf.writeShort(-1);
            return;
        }
        buf.writeShort(0);
        NBTTagCompound tagCompound = new NBTTagCompound();
        client.writeToNBT(tagCompound);
        ByteBufUtils.writeTag(buf, tagCompound);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        short s = buf.readShort();
        if(s == -1){
            return;
        }
        this.client = new PlayerHandlerClient();
        NBTTagCompound tagCompound = ByteBufUtils.readTag(buf);
        client.readFromNBT(tagCompound);
    }

    @Override
    public IMessage onMessage(PacketSyncRecord message, MessageContext ctx) {
        if(message.client == null && JustEnoughCalculation.proxy instanceof ProxyServer){
            //JustEnoughCalculation.proxy.getPlayerHandler().handleLogin(ctx.getServerHandler().playerEntity.getDisplayNameString());
            UUID uuida = ctx.getServerHandler().playerEntity.getUniqueID();
            UUID uuidb = ctx.getServerHandler().playerEntity.getPersistentID();
            return new PacketSyncRecord(((PlayerHandlerServer) JustEnoughCalculation.proxy.getPlayerHandler()).getClient(uuida));
        }if(message.client == null && JustEnoughCalculation.proxy instanceof ProxyClient){
            return null;
        }


        ((ProxyClient) JustEnoughCalculation.proxy).setPlayerHandler(message.client);
        return null;
    }
}
