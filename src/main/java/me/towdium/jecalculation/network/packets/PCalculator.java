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
    int slot;

    public PCalculator(PacketBuffer buf) {
        stack = buf.readItemStack();
        slot = buf.readInt();
    }

    public PCalculator(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    public void write(PacketBuffer buf) {
        buf.writeItemStack(stack);
        buf.writeInt(slot);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerInventory inventory = Objects.requireNonNull(ctx.get().getSender()).inventory;
            ItemStack calculator = inventory.getStackInSlot(slot);
            if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                inventory.setInventorySlotContents(slot, stack);
                return;
            }
            calculator = inventory.offHandInventory.get(0);
            if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                inventory.offHandInventory.set(0, stack);
            }
        });
    }
}