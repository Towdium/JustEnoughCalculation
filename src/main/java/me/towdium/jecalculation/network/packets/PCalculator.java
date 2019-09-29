package me.towdium.jecalculation.network.packets;

import me.towdium.jecalculation.JecaItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;


public class PCalculator {

    ItemStack stack;

    public PCalculator(PacketBuffer buf) {
        stack = buf.readItemStack();
    }

    public PCalculator(ItemStack stack) {
        this.stack = stack;
    }

    public void write(PacketBuffer buf) {
        buf.writeItemStack(stack);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerInventory inventory = Objects.requireNonNull(ctx.get().getSender()).inventory;
            ItemStack calculator = inventory.getCurrentItem();
            if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                inventory.setInventorySlotContents(inventory.currentItem, stack);
                return;
            }
            calculator = inventory.offHandInventory.get(0);
            if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                inventory.offHandInventory.set(0, stack);
            }
        });
    }
}