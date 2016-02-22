package pers.towdium.tudicraft.core;

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

    public ItemRecord(ItemStack itemStack, long amount){
        item = itemStack.getItem();
        meta = itemStack.getMetadata();
        this.amount = amount;
    }

    public ItemRecord(Item item, int meta, long amount) {
        this.item = item;
        this.meta = meta;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "itemRecord:" + item.getUnlocalizedName() + " x " + amount;
    }

    protected int getOutputAmount(Recipe recipe){
        for(int i = 0; i<4; i++){
            if(recipe.output[i].getItem() == item && recipe.output[i].getMetadata() == meta){
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
            return i!=0;
        }else {
            return false;
        }
    }

    public ItemRecord copy(){
        return new ItemRecord(item, meta, amount);
    }

    public ItemStack toItemStack(){
        ItemStack itemStack = new ItemStack(item, 1, meta);
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setLong("amount", amount);
        itemStack.setTagCompound(tagCompound);
        return itemStack;
    }
}
