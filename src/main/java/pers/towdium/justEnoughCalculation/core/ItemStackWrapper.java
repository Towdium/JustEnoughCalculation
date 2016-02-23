package pers.towdium.justEnoughCalculation.core;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author Towdium
 */
public class ItemStackWrapper {

    public static boolean isTypeEqual(ItemStack one, ItemStack two){
        return ItemStack.areItemsEqual(one, two);
    }

    public static boolean isStackEqual(ItemStack one, ItemStack two){
        return ItemStack.areItemStacksEqual(one, two);
    }

    public static boolean isSwapAcceptable(ItemStack in, ItemStack out){
        return !isTypeEqual(in, out) || getUnifiedAmount(in) < getUnifiedAmount(out);
    }

    public static ItemStack unify(ItemStack itemStack){
        if(itemStack == null){
            return null;
        }
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setInteger("count", getNormalAmount(itemStack));
        nbtTagCompound.setBoolean("approx", false);
        ItemStack buffer = new ItemStack(itemStack.getItem(), 1, itemStack.getMetadata());
        buffer.setTagCompound(nbtTagCompound);
        return buffer;
    }

    public static ItemStack unify(ItemStack itemStack, int amount){
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setInteger("count", amount*100);
        nbtTagCompound.setBoolean("approx", false);
        ItemStack buffer = new ItemStack(itemStack.getItem(), 1, itemStack.getMetadata());
        buffer.setTagCompound(nbtTagCompound);
        return buffer;
    }

    public static int getUnifiedAmount(ItemStack itemStack){
        if(itemStack.hasTagCompound()){
            return itemStack.getTagCompound().getInteger("count");
        }else {
            return itemStack.stackSize;
        }
    }

    public static boolean getUnifiedApprox(ItemStack itemStack) {
        return itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("approx");
    }

    public static int getNormalAmount(ItemStack itemStack){
        return itemStack.stackSize*100;
    }

    public static String getDisplayAmount(ItemStack itemStack){
        if(itemStack != null && itemStack.getTagCompound() != null){
            return itemStack.getTagCompound().getLong("amount")+"";
        }
        else return "";
    }

    public static class NBT {
        public static void initNBT(ItemStack itemStack){
            if(!itemStack.hasTagCompound()){
                itemStack.setTagCompound(new NBTTagCompound());
            }
        }

        public static void setItem(ItemStack dest, String key, ItemStack content){
            initNBT(dest);
            NBTTagCompound buffer = new NBTTagCompound();
            if (content != null){
                content.writeToNBT(buffer);
                dest.getTagCompound().setTag(key, buffer);
            }else {
                dest.getTagCompound().removeTag(key);
            }

        }

        public static ItemStack getItem(ItemStack itemStack, String key){
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if(tagCompound == null){
                return null;
            }
            NBTTagCompound itemTagCompound = tagCompound.getCompoundTag(key);
            if(itemTagCompound == null){
                return null;
            }
            return ItemStack.loadItemStackFromNBT(itemTagCompound);
        }

        public static void setString(ItemStack dest, String key, String value){
            initNBT(dest);
            dest.getTagCompound().setString(key, value);
        }

        public static String getString(ItemStack dest, String key){
            if(dest.getTagCompound() != null){
                return dest.getTagCompound().getString(key);
            }
            else return "";
        }

        public static long getLong(ItemStack dest, String key){
            if(dest.getTagCompound() != null){
                return dest.getTagCompound().getLong(key);
            }
            else return 0;
        }
    }
}
