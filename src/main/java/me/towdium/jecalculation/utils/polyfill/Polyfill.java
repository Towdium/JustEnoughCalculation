package me.towdium.jecalculation.utils.polyfill;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Polyfill {
    /**
     * Get an NBTTagCompound from this stack's NBT data.
     *
     * @param itemStack item stack
     * @param key       key
     * @param create    create if not exist
     * @return NBTTagCompound
     */
    public static NBTTagCompound getSubCompound(ItemStack itemStack, String key, boolean create) {
        if (itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey(key, 10)) {
            return itemStack.stackTagCompound.getCompoundTag(key);
        } else if (create) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            itemStack.setTagInfo(key, nbtTagCompound);
            return nbtTagCompound;
        } else {
            return null;
        }
    }
}
