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

    public PCalculator(FriendlyByteBuf buf) {
        stack = buf.readItem();
    }

    public PCalculator(ItemStack stack) {
        this.stack = stack;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Inventory inventory = Objects.requireNonNull(ctx.get().getSender()).getInventory();
            ItemStack calculator = inventory.getSelected();
            if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                inventory.setItem(inventory.selected, stack);
                return;
            }
            calculator = inventory.offhand.get(0);
            if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                inventory.offhand.set(0, stack);
            }
        });
    }
}