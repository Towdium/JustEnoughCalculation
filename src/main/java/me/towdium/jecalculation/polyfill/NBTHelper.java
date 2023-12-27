package me.towdium.jecalculation.polyfill;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

@ParametersAreNonnullByDefault
public class NBTHelper {

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

    public static Spliterator<NBTBase> spliterator(NBTTagList list) {
        List<NBTBase> tagList = (List<NBTBase>) list.tagList;
        return Spliterators.spliteratorUnknownSize(tagList.iterator(), 0);
    }

    public static NBTTagCompound getOrCreateSubCompound(ItemStack itemStack, String key) {
        if (itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey(key, 10)) {
            return itemStack.stackTagCompound.getCompoundTag(key);
        } else {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            itemStack.setTagInfo(key, nbtTagCompound);
            return nbtTagCompound;
        }
    }

    public static NBTTagCompound serializeNBT(ItemStack stack) {
        NBTTagCompound ret = new NBTTagCompound();
        stack.writeToNBT(ret);
        return ret;
    }
}
