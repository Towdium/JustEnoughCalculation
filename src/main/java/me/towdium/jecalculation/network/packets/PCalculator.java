package me.towdium.jecalculation.network.packets;

import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.JecaItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PCalculator implements IMessage {
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

    public static class Handler implements IMessageHandler<PCalculator, IMessage> {
        @Override
        public IMessage onMessage(PCalculator message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
            mainThread.addScheduledTask(() -> {
                InventoryPlayer inventory = ctx.getServerHandler().player.inventory;
                ItemStack calculator = inventory.getCurrentItem();
                if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                    inventory.setInventorySlotContents(inventory.currentItem, message.stack);
                    return;
                }
                calculator = inventory.offHandInventory.get(0);
                if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                    inventory.offHandInventory.set(0, message.stack);
                }
            });
            return null;
        }
    }
}