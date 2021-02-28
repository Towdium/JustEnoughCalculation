package me.towdium.jecalculation.core;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author Towdium
 */
public class ItemRecord{
    Item item;
    int meta;
    long amount;
    boolean approx;
    boolean locked;

    /**
     * Generate ItemRecord based on the ItemStack and multiply the amount
     */
    public ItemRecord(ItemStack itemStack, long multiplier){
        item = itemStack.getItem();
        meta = itemStack.getItemDamage();
        amount = ItemStackWrapper.getUnifiedAmount(itemStack) * multiplier;
        approx = itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("percentage");
    }

    public ItemRecord(ItemStack itemStack){
        this(itemStack, 1);
    }

    /**
     * Generate ItemRecord with customized amount
     */
    public ItemRecord(ItemStack itemStack, long amount, boolean approx){
        item = itemStack.getItem();
        meta = itemStack.getItemDamage();
        this.amount = amount;
        this.approx = approx;
    }

    public ItemRecord(Item item, int meta, long amount, boolean approx) {
        this.item = item;
        this.meta = meta;
        this.amount = amount;
        this.approx = approx;
    }

    @Override
    public String toString() {
        return "itemRecord:" + item.getUnlocalizedName() + " x " + amount;
    }

    protected int getOutputAmount(Recipe recipe){
        for(int i = 0; i<4; i++){
            if(recipe.output[i].getItem() == item && recipe.output[i].getItemDamage() == meta){
                return ItemStackWrapper.getUnifiedAmount(recipe.output[i]);
            }
        }
        return 0;
    }

    public boolean isSameType(ItemRecord record){
        return item == record.item && meta == record.meta;
    }

    public boolean add(ItemRecord record){
        if (isSameType(record)){
            amount += record.amount;
            approx = approx || record.approx;
            return true;
        }else {
            return false;
        }
    }

    public boolean minus(ItemRecord record){
        if (isSameType(record)){
            amount -= record.amount;
            return true;
        }else {
            return false;
        }
    }

    public boolean cancel(ItemRecord record){
        if (isSameType(record)){
            long i = amount>record.amount ? record.amount : amount;
            amount -= i;
            record.amount -= i;
            if(approx || record.approx){
                approx = true;
                record.approx = true;
            }
            return i!=0;
        }else {
            return false;
        }
    }

    public ItemRecord copy(){
        return new ItemRecord(item, meta, amount, approx);
    }

    public ItemStack toItemStack(){
        ItemStack itemStack = new ItemStack(item, 1, meta);
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setLong("amount", amount);
        if(approx){
            tagCompound.setBoolean("approx", true);
        }
        itemStack.setTagCompound(tagCompound);
        return itemStack;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ItemRecord){
            ItemRecord record = ((ItemRecord) obj);
            return record.approx == approx && record.item == item && record.amount == amount && record.meta == meta;
        }else {
            return false;
        }
    }
}
