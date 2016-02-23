package pers.towdium.justEnoughCalculation.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.network.PlayerHandlerClient;
import pers.towdium.justEnoughCalculation.network.PlayerHandlerServer;
import pers.towdium.justEnoughCalculation.network.ProxyClient;
import pers.towdium.justEnoughCalculation.network.ProxyServer;

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
