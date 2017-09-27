package me.towdium.jecalculation.core.labels.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LabelItemStack extends LabelSimpleAmount {
    public static final String IDENTIFIER = "oreDict";
    public static final String KEY_STACK = "stack";
    public static final String KEY_AMOUNT = "amount";

    ItemStack itemStack;

    public LabelItemStack(ItemStack is) {
        this(is, is.getCount());
    }

    // I will copy it!
    public LabelItemStack(ItemStack is, int amount) {
        itemStack = is.copy();
        this.amount = amount;
    }

    public LabelItemStack(NBTTagCompound nbt) {
        itemStack = new ItemStack(nbt.getCompoundTag(KEY_STACK));
        amount = nbt.getInteger(KEY_AMOUNT);
    }

    private LabelItemStack(LabelItemStack eis) {
        amount = eis.amount;
        itemStack = eis.itemStack;
    }

    @Override
    public List<String> getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(itemStack));
        return existing;
    }

    @Override
    public String getDisplayName() {
        return itemStack.getDisplayName();
    }

    @Override
    public ILabel copy() {
        return new LabelItemStack(this);
    }

    @Override
    public NBTTagCompound toNBTTagCompound() {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag(KEY_STACK, itemStack.writeToNBT(new NBTTagCompound()));
        ret.setInteger(KEY_AMOUNT, amount);
        return ret;
    }

    @Override
    public void drawEntry(JecGui gui) {
        gui.drawItemStack(0, 0, itemStack, false);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LabelItemStack && itemStack.equals(((LabelItemStack) obj).itemStack);
    }
}
