package me.towdium.jecalculation.core.labels.labels;

import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-11.
 */
@ParametersAreNonnullByDefault
public abstract class LabelSimpleAmount implements ILabel {
    public static final String KEY_AMOUNT = "amount";

    protected int amount;

    public LabelSimpleAmount(int amount) {
        this.amount = amount;
    }

    public LabelSimpleAmount(LabelSimpleAmount lsa) {
        this(lsa.amount);
    }

    public LabelSimpleAmount(NBTTagCompound nbt) {
        amount = nbt.getInteger(KEY_AMOUNT);
    }

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

    @Override
    public NBTTagCompound toNBTTagCompound() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger(KEY_AMOUNT, amount);
        return nbt;
    }

    public int getAmount() {
        return amount;
    }
}
