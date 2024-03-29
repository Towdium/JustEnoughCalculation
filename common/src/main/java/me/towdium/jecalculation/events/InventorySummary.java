package me.towdium.jecalculation.events;

import me.towdium.jecalculation.JecaItem;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InventorySummary {

    protected List<ItemAmount> amounts = new ArrayList<>();

    public InventorySummary(Inventory inventory) {
        inventory.armor.forEach(this::addItemStack);
        inventory.items.forEach(this::addItemStack);
        inventory.offhand.forEach(this::addItemStack);
    }

    protected void addItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        Optional<ItemAmount> existingAmount = amounts.stream()
                .filter((item) -> isStackTypeEqual(item.stack, stack))
                .findFirst();

        if (existingAmount.isPresent()) {
            existingAmount.get().amount += stack.getCount();
        } else {
            amounts.add(new ItemAmount(stack, stack.getCount()));
        }
    }

    protected boolean isStackTypeEqual(ItemStack first, ItemStack second) {
        if (first.getItem() != second.getItem()) {
            return false;
        } else if (first.getItem() instanceof JecaItem && second.getItem() instanceof JecaItem) {
            return first.getItem() == second.getItem();
        } else {
            return Utilities.areCapsCompatible(first, second);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventorySummary that = (InventorySummary) o;

        if (this.amounts.size() != that.amounts.size()) {
            return false;
        }

        return that.amounts.stream().allMatch((test) -> {
            Optional<ItemAmount> thisAmount = amounts.stream()
                    .filter((item) -> isStackTypeEqual(item.stack, test.stack))
                    .findFirst();
            return thisAmount.isPresent() && test.amount == thisAmount.get().amount;
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(amounts);
    }

    private static class ItemAmount {
        ItemStack stack;
        int amount;

        ItemAmount(ItemStack stack, int amount) {
            this.stack = stack;
            this.amount = amount;
        }
    }
}
