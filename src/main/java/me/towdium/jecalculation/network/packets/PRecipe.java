package me.towdium.jecalculation.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.event.handlers.ControllerServer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class PRecipe implements IMessage, IMessageHandler<PRecipe, IMessage> {
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

    @Override
    public IMessage onMessage(PRecipe message, MessageContext ctx) {
        UUID uuid = ctx.getServerHandler().playerEntity.getUniqueID();
        ControllerServer.modify(uuid, message.group, message.index, message.recipe);
        return null;
    }
}
