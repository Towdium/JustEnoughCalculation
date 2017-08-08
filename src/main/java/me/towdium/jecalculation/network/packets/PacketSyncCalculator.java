package me.towdium.jecalculation.network.packets;

import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: Towdium
 * Date:   2016/8/12.
 */
public class PacketSyncCalculator implements IMessage, IMessageHandler<PacketSyncCalculator, IMessage> {
    ItemStack stack;

    public PacketSyncCalculator() {
    }

    public PacketSyncCalculator(ItemStack stack) {
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
    public IMessage onMessage(PacketSyncCalculator message, MessageContext ctx) {
        InventoryPlayer inventory = ctx.getServerHandler().player.inventory;
        ItemStack calculator = inventory.getCurrentItem();
        if (!calculator.isEmpty() && calculator.getItem() == JustEnoughCalculation.itemCalculator) {
            inventory.setInventorySlotContents(inventory.currentItem, message.stack);
        } else {
            calculator = inventory.offHandInventory.get(0);
            if (!calculator.isEmpty() && calculator.getItem() == JustEnoughCalculation.itemCalculator) {
                inventory.offHandInventory.set(0, message.stack);
            }
        }
        return null;
    }
}
