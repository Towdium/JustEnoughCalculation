package me.towdium.jecalculation.network.packets;

import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.item.ItemCalculator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PSyncCalculator implements IMessage, IMessageHandler<PSyncCalculator, IMessage> {
    ItemStack stack;

    public PSyncCalculator() {
    }

    public PSyncCalculator(ItemStack stack) {
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
    public IMessage onMessage(PSyncCalculator message, MessageContext ctx) {
        InventoryPlayer inventory = ctx.getServerHandler().player.inventory;
        ItemStack calculator = inventory.getCurrentItem();
        if (!calculator.isEmpty() && calculator.getItem() instanceof ItemCalculator) {
            inventory.setInventorySlotContents(inventory.currentItem, message.stack);
            return null;
        }
        calculator = inventory.offHandInventory.get(0);
        if (!calculator.isEmpty() && calculator.getItem() instanceof ItemCalculator) {
            inventory.offHandInventory.set(0, message.stack);
        }
        return null;
    }
}