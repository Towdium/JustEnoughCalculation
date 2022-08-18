package me.towdium.jecalculation.data.structure;

import net.minecraft.nbt.CompoundTag;

/**
 * Author: Towdium
 * Date: 19-1-21
 */
public class RecordPlayer implements IRecord {
    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_LAST = "last";

    public Recipes recipes;
    public String last;  // last group edited

    public RecordPlayer() {
        recipes = new Recipes();
    }

    public RecordPlayer(CompoundTag nbt) {
        this();
        deserialize(nbt);
    }

    public void deserialize(CompoundTag tag) {
        recipes.deserialize(tag.getCompound(KEY_RECIPES));
        last = tag.getString(KEY_LAST);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag ret = new CompoundTag();
        if (last != null) ret.putString(KEY_LAST, last);
        ret.put(KEY_RECIPES, recipes.serialize());
        return ret;
    }
}
