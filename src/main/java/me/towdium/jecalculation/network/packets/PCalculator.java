package me.towdium.jecalculation.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.item.ItemCalculator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class PCalculator implements IMessage, IMessageHandler<PCalculator, IMessage> {
    ItemStack stack;

    public PCalculator() {
    }

    public PCalculator(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
    }

    @Override
    public IMessage onMessage(PCalculator message, MessageContext ctx) {
        InventoryPlayer inventory = ctx.getServerHandler().playerEntity.inventory;
        ItemStack calculator = inventory.getCurrentItem();
        if (calculator != null && calculator.getItem() instanceof ItemCalculator) {
            inventory.setInventorySlotContents(inventory.currentItem, message.stack);
            return null;
        }
        return null;
    }
}