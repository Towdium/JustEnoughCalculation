package me.towdium.jecalculation.data.structure;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Author: Towdium
 * Date: 19-1-21
 */
public class RecordPlayer implements IRecord {
    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_LAST = "last";

    public Recipes recipes;
    public String last;

    public RecordPlayer() {
        recipes = new Recipes();
    }

    public RecordPlayer(NBTTagCompound nbt) {
        recipes = new Recipes(nbt.getCompoundTag(KEY_RECIPES));
        last = nbt.getString(KEY_LAST);
    }

    @Override
    public NBTTagCompound serialize() {
        NBTTagCompound ret = new NBTTagCompound();
        if (last != null) ret.setString(KEY_LAST, last);
        ret.setTag(KEY_RECIPES, recipes.serialize());
        return ret;
    }
}
