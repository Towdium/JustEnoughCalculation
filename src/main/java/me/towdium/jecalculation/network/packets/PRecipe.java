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

public class PRecipe implements IMessage {
    static final String KEY_GROUP = "group";
    static final String KEY_INDEX = "index";
    static final String KEY_RECIPE = "recipe";

    String group;
    int index;
    Recipe recipe;

    public PRecipe() {
    }

    public PRecipe(String group, int index, Recipe recipe) {
        this.group = group;
        this.index = index;
        this.recipe = recipe;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        tag = tag == null ? new NBTTagCompound() : tag;
        group = tag.getString(KEY_GROUP);
        index = tag.getInteger(KEY_INDEX);
        recipe = tag.hasKey(KEY_RECIPE) ? new Recipe(tag.getCompoundTag(KEY_RECIPE)) : null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(KEY_GROUP, group);
        tag.setInteger(KEY_INDEX, index);
        if (recipe != null) tag.setTag(KEY_RECIPE, recipe.serialize());
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler implements IMessageHandler<PRecipe, IMessage> {
        @Override
        public IMessage onMessage(PRecipe message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
            mainThread.addScheduledTask(() -> JecaCapability.getRecipes(ctx.getServerHandler().player)
                    .modify(message.group, message.index, message.recipe));
            return null;
        }
    }
}
