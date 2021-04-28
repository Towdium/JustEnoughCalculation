package me.towdium.jecalculation.core.labels.labels;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.core.labels.ILabel;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
public class LabelItemStack extends LabelSimpleAmount {
    public static final String IDENTIFIER = "oreDict";
    public static final String KEY_STACK = "stack";
    public static final String KEY_AMOUNT = "amount";

    ItemStack itemStack;

    public LabelItemStack(ItemStack is) {
        this(is, is.stackSize);
    }

    // I will copy it!
    public LabelItemStack(ItemStack is, int amount) {
        itemStack = is.copy();
        this.amount = amount;
    }

    public LabelItemStack(NBTTagCompound nbt) {
        itemStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(KEY_STACK));
        amount = nbt.getInteger(KEY_AMOUNT);
    }

    private LabelItemStack(LabelItemStack eis) {
        amount = eis.amount;
        itemStack = eis.itemStack;
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
}
