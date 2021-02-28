package me.towdium.jecalculation.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.item.ItemCalculator;
import net.minecraft.item.ItemStack;

/**
 * @author Towdium
 */
public class PacketCalculatorUpdate implements IMessage, IMessageHandler<PacketCalculatorUpdate, IMessage> {
    ItemStack itemStack;

    public PacketCalculatorUpdate() {
    }

    public PacketCalculatorUpdate(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        itemStack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, itemStack);
    }

    @Override
    public IMessage onMessage(PacketCalculatorUpdate message, MessageContext ctx) {
        if (ctx.getServerHandler().playerEntity.getHeldItem().getItem() instanceof ItemCalculator) {
            ctx.getServerHandler().playerEntity.inventory
                    .setInventorySlotContents(ctx.getServerHandler().playerEntity.inventory.currentItem,
                                              message.itemStack);
        }
        return null;
    }
}
