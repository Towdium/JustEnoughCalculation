package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
@ParametersAreNonnullByDefault
public class LItemStack extends LabelSimpleAmount {
    public static final String IDENTIFIER = "itemStack";
    public static final String KEY_STACK = "stack";

    ItemStack itemStack;

    public LItemStack(ItemStack is) {
        this(is, is.stackSize);
    }

    // I will copy it!
    public LItemStack(ItemStack is, int amount) {
        super(amount);
        itemStack = is.copy();
    }

    public LItemStack(NBTTagCompound nbt) {
        super(nbt);
        itemStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(KEY_STACK));
    }

    private LItemStack(LItemStack lis) {
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
        return itemStack.getItem() == null ? "" : itemStack.getDisplayName();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ILabel copy() {
        return new LItemStack(this);
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
        return obj instanceof LItemStack
               && ItemStack.areItemStacksEqual(itemStack, ((LItemStack) obj).itemStack)
               && amount == ((LItemStack) obj).amount;
    }


    @Override
    public int hashCode() {
        return itemStack.getItemDamage() ^ itemStack.getItem().getUnlocalizedName().hashCode() ^ amount
               ^ (itemStack.getTagCompound() == null ? 0 : itemStack.getTagCompound().hashCode());
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
