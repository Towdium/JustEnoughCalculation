package me.towdium.jecalculation.data.label.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    ItemStack itemStack;

    public LabelItemStack(ItemStack is) {
        this(is, is.getCount());
    }

    // I will copy it!
    public LabelItemStack(ItemStack is, int amount) {
        super(amount);
        itemStack = is.copy();
    }

    public LabelItemStack(NBTTagCompound nbt) {
        super(nbt);
        itemStack = new ItemStack(nbt.getCompoundTag(KEY_STACK));
    }

    private LabelItemStack(LabelItemStack lis) {
        super(lis);
        itemStack = lis.itemStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(itemStack));
        return existing;
    }

    @Override
    @SideOnly(Side.CLIENT)
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
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecGui gui) {
        gui.drawItemStack(0, 0, itemStack, false);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LabelItemStack
                && itemStack.equals(((LabelItemStack) obj).itemStack)
                && amount == ((LabelItemStack) obj).amount;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
