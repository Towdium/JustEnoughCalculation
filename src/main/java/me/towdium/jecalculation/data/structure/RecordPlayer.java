package me.towdium.jecalculation.data.structure;

import net.minecraft.nbt.CompoundNBT;

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

    public RecordPlayer(CompoundNBT nbt) {
        recipes = new Recipes(nbt.getCompound(KEY_RECIPES));
        last = nbt.getString(KEY_LAST);
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT ret = new CompoundNBT();
        if (last != null) ret.putString(KEY_LAST, last);
        ret.put(KEY_RECIPES, recipes.serialize());
        return ret;
    }
}
