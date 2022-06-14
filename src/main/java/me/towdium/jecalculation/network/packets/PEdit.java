package me.towdium.jecalculation.network.packets;

import me.towdium.jecalculation.JecaCapability;
import me.towdium.jecalculation.data.structure.Recipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

public class PEdit {
    static final String KEY_OLD = "old";
    static final String KEY_NEW = "new";
    static final String KEY_INDEX = "index";
    static final String KEY_RECIPE = "recipe";

    String old, neu;
    int index;
    Recipe recipe;

    public PEdit() {
    }

    // set recipe: Y Y/null Y Y
    // rename group: Y Y -1 null
    // add recipe: Y null -1 Y
    // remove recipe: Y null Y null
    // remove group: Y null -1 null
    public PEdit(String neu, @Nullable String old, int index, @Nullable Recipe recipe) {
        this.neu = neu;
        this.old = old;
        this.index = index;
        this.recipe = recipe;
    }

    public PEdit(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        tag = tag == null ? new CompoundTag() : tag;
        old = tag.contains(KEY_OLD) ? tag.getString(KEY_OLD) : null;
        neu = tag.getString(KEY_NEW);
        index = tag.getInt(KEY_INDEX);
        recipe = tag.contains(KEY_RECIPE) ? new Recipe(tag.getCompound(KEY_RECIPE)) : null;
    }

    public void write(FriendlyByteBuf buf) {
        CompoundTag tag = new CompoundTag();
        if (old != null) tag.putString(KEY_OLD, old);
        tag.putString(KEY_NEW, neu);
        tag.putInt(KEY_INDEX, index);
        if (recipe != null) tag.put(KEY_RECIPE, recipe.serialize());
        buf.writeNbt(tag);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            JecaCapability.getRecord(Objects.requireNonNull(ctx.get().getSender())).recipes
                    .modify(neu, old, index, recipe);
            JecaCapability.getRecord(Objects.requireNonNull(ctx.get().getSender())).last = old;
        });
    }
}
