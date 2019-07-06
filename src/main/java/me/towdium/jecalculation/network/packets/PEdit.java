package me.towdium.jecalculation.network.packets;

import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.JecaCapability;
import me.towdium.jecalculation.data.structure.Recipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class PEdit implements IMessage {
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

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        tag = tag == null ? new NBTTagCompound() : tag;
        old = tag.hasKey(KEY_OLD) ? tag.getString(KEY_OLD) : null;
        neu = tag.getString(KEY_NEW);
        index = tag.getInteger(KEY_INDEX);
        recipe = tag.hasKey(KEY_RECIPE) ? new Recipe(tag.getCompoundTag(KEY_RECIPE)) : null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        if (old != null) tag.setString(KEY_OLD, old);
        tag.setString(KEY_NEW, neu);
        tag.setInteger(KEY_INDEX, index);
        if (recipe != null) tag.setTag(KEY_RECIPE, recipe.serialize());
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler implements IMessageHandler<PEdit, IMessage> {
        @Override
        public IMessage onMessage(PEdit message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
            mainThread.addScheduledTask(() -> {
                JecaCapability.getRecord(ctx.getServerHandler().player).recipes
                        .modify(message.neu, message.old, message.index, message.recipe);
                JecaCapability.getRecord(ctx.getServerHandler().player).last = message.old;
            });
            return null;
        }
    }
}
