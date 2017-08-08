package me.towdium.jecalculation.network.packets;

import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.network.IProxy;
import me.towdium.jecalculation.network.PlayerHandlerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

/**
 * Author: Towdium
 * Date:   2016/9/1.
 */
public class PacketOredictModify implements IMessage, IMessageHandler<PacketOredictModify, IMessage> {
    boolean add;
    ItemStack stack;

    public PacketOredictModify() {
    }

    public PacketOredictModify(boolean add, ItemStack stack) {
        this.add = add;
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        add = buf.readBoolean();
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(add);
        ByteBufUtils.writeItemStack(buf, stack);
    }

    @Override
    public IMessage onMessage(PacketOredictModify message, MessageContext ctx) {
        IProxy.IPlayerHandler handler = JustEnoughCalculation.proxy.getPlayerHandler();
        if (handler instanceof PlayerHandlerMP) {
            PlayerHandlerMP handlerMP = ((PlayerHandlerMP) handler);
            UUID uuid = ctx.getServerHandler().player.getUniqueID();
            if (message.add) {
                handlerMP.addOredictPref(uuid, message.stack);
            } else {
                handlerMP.removeOredictPref(uuid, message.stack);
            }
        }
        return null;
    }
}
