package me.towdium.jecalculation.core.labels.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.Utilities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-11.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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
    public List<String> getToolTip(List<String> existing, boolean detailed) {
        if (detailed) existing.add(FORMAT_GREY +
                Utilities.L18n.format("label.common.tooltip.amount", Integer.toString(amount)));
        return existing;
    }

    @Override
    public String getAmountString() {
        return amount == 0 ? "" : Utilities.cutNumber(amount, 5);
    }
}
