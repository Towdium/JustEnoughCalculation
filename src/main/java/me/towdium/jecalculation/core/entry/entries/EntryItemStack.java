package me.towdium.jecalculation.core.entry.entries;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.core.entry.Entry;
import net.minecraft.item.ItemStack;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
public class EntryItemStack extends EntrySimpleAmount {
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

    private EntryItemStack(EntryItemStack eis) {
        amount = eis.amount;
        itemStack = eis.itemStack;
    }

    @Override
    public Entry copy() {
        return new EntryItemStack(this);
    }

    @Override
    public void drawEntry(JecGui gui) {
        gui.drawItemStack(0, 0, itemStack, false);
    }
}
