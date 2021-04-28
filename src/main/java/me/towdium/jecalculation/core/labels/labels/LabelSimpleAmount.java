package me.towdium.jecalculation.core.labels.labels;

import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.Utilities;

/**
 * Author: towdium
 * Date:   17-9-11.
 */
public abstract class LabelSimpleAmount implements ILabel {
    protected int amount;

    @Override
    public ILabel increaseAmount() {
        amount++;
        return this;
    }

    @Override
    public ILabel increaseAmountLarge() {
        amount += 10;
        return this;
    }

    @Override
    public ILabel decreaseAmount() {
        if (amount <= 1) return ILabel.EMPTY;
        else {
            amount--;
            return this;
        }
    }

    @Override
    public ILabel decreaseAmountLarge() {
        if (amount <= 10) return ILabel.EMPTY;
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
