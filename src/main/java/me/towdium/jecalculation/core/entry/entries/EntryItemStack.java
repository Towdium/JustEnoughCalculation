package me.towdium.jecalculation.core.entry.entries;

import me.towdium.jecalculation.core.entry.Entry;
import net.minecraft.item.ItemStack;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
public class EntryItemStack implements Entry {
    int amount;
    ItemStack itemStack;

    public EntryItemStack(ItemStack is) {
        this(is, is.getCount());
    }

    // I will copy it!
    public EntryItemStack(ItemStack is, int amount) {
        itemStack = is.copy();
        this.amount = amount;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public ItemStack getRepresentation() {
        return itemStack;
    }

    @Override
    public String getAmountString() {
        return Integer.toString(amount);  // TODO format
    }
}
