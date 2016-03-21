package pers.towdium.justEnoughCalculation.core;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;

/**
 * @author Towdium
 */
public class ItemStackWrapper {

    public static boolean isTypeEqual(ItemStack one, ItemStack two){
        return one != null && two != null && one.getItem() == two.getItem() && one.getItemDamage() == two.getItemDamage();
    }

    public static boolean isStackEqual(ItemStack one, ItemStack two){
        return ItemStack.areItemStacksEqual(one, two);
    }

    public static int getUnifiedAmount(ItemStack itemStack){
        if(itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("percentage")){
            return itemStack.getTagCompound().getInteger("percentage");
        }else {
            return itemStack.stackSize*100;
        }
    }

    public static String getDisplayAmount(ItemStack itemStack){
        if(itemStack != null){
            if(itemStack.getTagCompound() != null){
                if(itemStack.getTagCompound().getBoolean("mark")){
                    return "";
                }
                long l = itemStack.getTagCompound().hasKey("amount") ? itemStack.getTagCompound().getLong("amount") : -1;
                if(l == 0 && itemStack.stackSize == 0){
                    return "";
                }
                int i = itemStack.getTagCompound().getInteger("percentage");
                if(i != 0){
                    return i + "%";
                }else{
                    if(l==-1){
                        return String.valueOf(itemStack.stackSize);
                    }
                    return getSimplifiedString((l+99)/100, itemStack.getTagCompound().getBoolean("approx"));
                }
            }else {
                return String.valueOf(itemStack.stackSize);
            }
        }
        else return "";
    }

    public static ItemStack toPercentage(ItemStack itemStack){
        NBT.setInt(itemStack, "percentage", itemStack.stackSize*100);
        itemStack.stackSize = 1;
        return itemStack;
    }

    public static ItemStack toNormal(ItemStack itemStack){
        if(itemStack.hasTagCompound()){
            int i = itemStack.getTagCompound().getInteger("percentage");
            itemStack.stackSize = (i+99)/100;
            itemStack.setTagCompound(null);
        }else {
            itemStack.stackSize = 1;
        }
        itemStack.setTagCompound(null);
        ItemStackWrapper.NBT.setBool(itemStack, JustEnoughCalculation.Reference.MODID, true);
        return itemStack;
    }

    public static String getSimplifiedString(long l, boolean approx){
        String s;
        if(approx){
            if(l<999){
                s = String.valueOf(l);
            }else if(l<9999){
                s = String.format("≈%.1fK", (double)l/1000);
            }else if(l<99999){
                s = String.format("≈%.0fK", (double)l/1000);
            }else if(l<999999){
                s = String.format("≈%.1fM", (double)l/1000000);
            }else if(l<9999999){
                s = String.format("≈%.1fM", (double)l/1000000);
            }else if(l<99999999){
                s = String.format("≈%.0fM", (double)l/1000000);
            }else if(l<999999999){
                s = String.format("≈%.1fB", (double)l/1000000000);
            }else if(l<9999999999L){
                s = String.format("≈%.1fB", (double)l/1000000000);
            }else if(l<99999999999L){
                s = String.format("≈%.0fB", (double)l/1000000000);
            }else if(l<999999999999L){
                s = String.format("≈%.1fT", (double)l/1000000000000L);
            }else if(l<9999999999999L){
                s = String.format("≈%.1fT", (double)l/1000000000000L);
            }else if(l<99999999999999L){
                s = String.format("≈%.0fT", (double)l/1000000000000L);
            }else if(l<999999999999999L){
                s = String.format("≈%.1fG", (double)l/1000000000000000L);
            }else if(l<9999999999999999L){
                s = String.format("≈%.1fG", (double)l/1000000000000000L);
            }else {
                s = String.format("≈%.0fG", (double)l/1000000000000000L);
            }
        }else {
            if(l<999){
                s = String.valueOf(l);
            }else if(l<9999){
                s = String.format("%.2fK", (double)l/1000);
            }else if(l<99999){
                s = String.format("%.1fK", (double)l/1000);
            }else if(l<999999){
                s = String.format("%.0fK", (double)l/1000);
            }else if(l<9999999){
                s = String.format("%.2fM", (double)l/1000000);
            }else if(l<99999999){
                s = String.format("%.1fM", (double)l/1000000);
            }else if(l<999999999){
                s = String.format("%.0fB", (double)l/1000000);
            }else if(l<9999999999L){
                s = String.format("%.2fB", (double)l/1000000000);
            }else if(l<99999999999L){
                s = String.format("%.1fB", (double)l/1000000000);
            }else if(l<999999999999L){
                s = String.format("%.0fT", (double)l/1000000000);
            }else if(l<9999999999999L){
                s = String.format("%.2fT", (double)l/1000000000000L);
            }else if(l<99999999999999L){
                s = String.format("%.1fT", (double)l/1000000000000L);
            }else if(l<999999999999999L){
                s = String.format("%.0fG", (double)l/1000000000000L);
            }else if(l<9999999999999999L){
                s = String.format("%.2fG", (double)l/1000000000000000L);
            }else if(l<99999999999990999L){
                s = String.format("%.1fG", (double)l/1000000000000000L);
            }else {
                s = String.format("%.0fG", (double)l/1000000000000000L);
            }
        }
        return s;
    }

    public static long getGhostItemAmount(ItemStack itemStack){
        if (itemStack.hasTagCompound()){
            long l = itemStack.getTagCompound().getLong("amount");
            return (l+99)/100;
        }else {
            return 0;
        }

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

        public static void setBool(ItemStack dest, String key, boolean value){
            initNBT(dest);
            dest.getTagCompound().setBoolean(key, value);
        }

        public static void setLong(ItemStack dest, String key, long value){
            initNBT(dest);
            dest.getTagCompound().setLong(key, value);
        }

        public static String getString(ItemStack dest, String key){
            if(dest.getTagCompound() != null){
                return dest.getTagCompound().getString(key);
            }
            else return "";
        }

        public static boolean getBool(ItemStack dest, String key) {
            return dest.getTagCompound() != null && dest.getTagCompound().getBoolean(key);
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
                    if(itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("percentage")){
                        itemStack.getTagCompound().setInteger("percentage", itemStack.getTagCompound().getInteger("percentage")+1);
                    }else {
                        itemStack.stackSize += 1;
                    }
                }
                return true;
            }
        }

        public static boolean leftShift(ItemStack itemStack, boolean doChange){
            if(itemStack == null || itemStack.stackSize>=64){
                return false;
            }else {
                if(doChange){
                    if(itemStack.hasTagCompound()&& itemStack.getTagCompound().hasKey("percentage")){
                        int i = itemStack.getTagCompound().getInteger("percentage");
                        itemStack.getTagCompound().setInteger("percentage", i == 1 ? 10 : i+10);
                    }else {
                        if(itemStack.stackSize == 1){
                            itemStack.stackSize = 10;
                        }else if(itemStack.stackSize <= 54){
                            itemStack.stackSize += 10;
                        }else {
                            itemStack.stackSize = 64;
                        }
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
                    if(itemStack.hasTagCompound()&& itemStack.getTagCompound().hasKey("percentage")){
                        int i = itemStack.getTagCompound().getInteger("percentage");
                        if(i == 1 || i == 0){
                            itemStack.stackSize = 0;
                            return true;
                        }
                        itemStack.getTagCompound().setInteger("percentage", --i);
                    }else {
                        itemStack.stackSize -= 1;
                    }
                }
                return true;
            }
        }

        public static boolean rightShift(ItemStack itemStack, boolean doChange){
            if(itemStack == null){
                return false;
            }else {
                if(doChange){
                    if(itemStack.hasTagCompound()&& itemStack.getTagCompound().hasKey("percentage")){
                        int i = itemStack.getTagCompound().getInteger("percentage");
                        if(i == 0 || i == 1){
                            itemStack.stackSize = 0;
                            return true;
                        }
                        itemStack.getTagCompound().setInteger("percentage", i <= 10 ? 1 : i-10);
                    }else {
                        if(itemStack.stackSize == 1){
                            itemStack.stackSize = 0;
                        }else if(itemStack.stackSize <= 10){
                            itemStack.stackSize = 1;
                        } else {
                            itemStack.stackSize -= 10;
                        }
                    }
                }
                return true;
            }
        }
    }
}
