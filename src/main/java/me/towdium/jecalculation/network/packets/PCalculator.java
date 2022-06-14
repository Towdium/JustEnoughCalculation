package me.towdium.jecalculation.network.packets;

import me.towdium.jecalculation.JecaItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;


public class PCalculator {

    ItemStack stack;
    int slot;

    public PCalculator(FriendlyByteBuf buf) {
        stack = buf.readItem();
        slot = buf.readInt();
    }

    public PCalculator(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeItem(stack);
        buf.writeInt(slot);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Inventory inventory = Objects.requireNonNull(ctx.get().getSender()).getInventory();
            ItemStack calculator = inventory.getItem(slot);
            if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                inventory.setItem(slot, stack);
                return;
            }
            calculator = inventory.offhand.get(0);
            if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                inventory.offhand.set(0, stack);
            }
        });
    }
}