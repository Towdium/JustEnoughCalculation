package me.towdium.jecalculation.core.entry.entries;

import me.towdium.jecalculation.core.entry.Entry;
import me.towdium.jecalculation.utils.Utilities;

/**
 * Author: towdium
 * Date:   17-9-11.
 */
public abstract class EntrySimpleAmount implements Entry {
    protected int amount;

    @Override
    public Entry increaseAmount() {
        amount++;
        return this;
    }

    @Override
    public Entry increaseAmountLarge() {
        amount += 10;
        return this;
    }

    @Override
    public Entry decreaseAmount() {
        if (amount <= 1) return Entry.EMPTY;
        else {
            amount--;
            return this;
        }
    }

    @Override
    public Entry decreaseAmountLarge() {
        if (amount <= 10) return Entry.EMPTY;
        else {
            amount -= 10;
            return this;
        }
    }

    @Override
    public String getAmountString() {
        return amount == 0 ? "" : Utilities.cutLong(amount, 5);
    }

}
