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

    public static int getUnifiedAmount(ItemStack itemStack){
        if(itemStack.hasTagCompound()){
            return itemStack.getTagCompound().getInteger("percentage");
        }else {
            return itemStack.stackSize*100;
        }
    }

    public static int getNormalAmount(ItemStack itemStack){
        return itemStack.stackSize*100;
    }

    public static String getDisplayAmount(ItemStack itemStack){
        if(itemStack != null && itemStack.getTagCompound() != null){
            long l = itemStack.getTagCompound().getLong("amount");
            if(l%100==0){
                return String.valueOf(l/100);
            }else {
                return String.valueOf(l/100 + 1);
            }
        }
        else return "";
    }

    public static ItemStack toPercentage(ItemStack itemStack){
        NBT.setInt(itemStack, "percentage", 50);
        return itemStack;
    }

    public static ItemStack toNormal(ItemStack itemStack){
        itemStack.setTagCompound(null);
        itemStack.stackSize = 1;
        return itemStack;
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

        public static void setInt(ItemStack dest, String key, int value){
            initNBT(dest);
            dest.getTagCompound().setInteger(key, value);
        }

        public static String getString(ItemStack dest, String key){
            if(dest.getTagCompound() != null){
                return dest.getTagCompound().getString(key);
            }
            else return "";
        }

        public static int getInt(ItemStack dest, String key){
            if(dest.getTagCompound() != null){
                return dest.getTagCompound().getInteger(key);
            }
            else return 0;
        }

        public static long getLong(ItemStack dest, String key){
            if(dest.getTagCompound() != null){
                return dest.getTagCompound().getLong(key);
            }
            else return 0;
        }
    }

    public static class Click {
        public static boolean leftClick(ItemStack itemStack, boolean doChange){
            if(itemStack == null || itemStack.stackSize>=64){
                return false;
            }else {
                if(doChange){
                    itemStack.stackSize += 1;
                }
                return true;
            }
        }

        public static boolean leftShift(ItemStack itemStack, boolean doChange){
            if(itemStack == null || itemStack.stackSize>=64){
                return false;
            }else {
                if(doChange){
                    if(itemStack.stackSize == 1){
                        itemStack.stackSize = 10;
                    }else if(itemStack.stackSize <= 54){
                        itemStack.stackSize += 10;
                    }else {
                        itemStack.stackSize = 64;
                    }
                }
                return true;
            }
        }

        public static boolean rightClick(ItemStack itemStack, boolean doChange){
            if(itemStack == null){
                return false;
            }else {
                if(doChange){
                    itemStack.stackSize -= 1;
                }
                return true;
            }
        }

        public static boolean rightShift(ItemStack itemStack, boolean doChange){
            if(itemStack == null || itemStack.stackSize>=64){
                return false;
            }else {
                if(doChange){
                    if(itemStack.stackSize <= 10){
                        itemStack.stackSize = 0;
                    } else {
                        itemStack.stackSize -= 10;
                    }
                }
                return true;
            }
        }
    }
}
