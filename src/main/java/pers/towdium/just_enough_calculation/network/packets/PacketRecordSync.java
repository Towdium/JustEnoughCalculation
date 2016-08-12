package pers.towdium.just_enough_calculation.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import pers.towdium.just_enough_calculation.network.PlayerHandlerSP;
import pers.towdium.just_enough_calculation.util.PlayerRecordHelper;

/**
 * Author: Towdium
 * Date:   2016/8/12.
 */
public class PacketRecordSync implements IMessage, IMessageHandler<PacketRecordSync, IMessage> {
    PlayerHandlerSP playerHandler;

    public PacketRecordSync() {
    }

    public PacketRecordSync(PlayerHandlerSP playerHandler) {
        this.playerHandler = playerHandler;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerHandler = new PlayerHandlerSP();
        playerHandler.readFromNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, playerHandler.writeToNBT());
    }

    @Override
    public IMessage onMessage(PacketRecordSync message, MessageContext ctx) {
        PlayerRecordHelper.setPlayerHandler(message.playerHandler);
        return null;
    }
}
