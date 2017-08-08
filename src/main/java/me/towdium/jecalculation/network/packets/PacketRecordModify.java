package me.towdium.jecalculation.network.packets;

import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.core.Recipe;
import me.towdium.jecalculation.network.IProxy;
import me.towdium.jecalculation.network.PlayerHandlerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;
import java.util.UUID;

/**
 * Author: Towdium
 * Date:   2016/8/12.
 */
public class PacketRecordModify implements IMessage, IMessageHandler<PacketRecordModify, IMessage> {
    int index;
    String group;
    String groupOld;
    Recipe recipe;

    public PacketRecordModify() {
    }

    public PacketRecordModify(int index, String group, String groupOld, Recipe recipe) {
        this.index = index;
        this.group = group;
        this.groupOld = groupOld;
        this.recipe = recipe;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        Objects.requireNonNull(tag);
        index = tag.getInteger("index");
        group = tag.getString("group");
        groupOld = tag.getString("groupOld");
        NBTTagCompound r = tag.getCompoundTag("recipe");
        recipe = r.hasNoTags() ? null : new Recipe(r);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("index", index);
        tag.setString("group", group);
        tag.setString("groupOld", groupOld);
        tag.setTag("recipe", recipe == null ? new NBTTagCompound() : recipe.writeToNbt());
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public IMessage onMessage(PacketRecordModify message, MessageContext ctx) {
        IProxy.IPlayerHandler handler = JustEnoughCalculation.proxy.getPlayerHandler();
        if (handler instanceof PlayerHandlerMP) {
            PlayerHandlerMP handlerMP = ((PlayerHandlerMP) handler);
            UUID uuid = ctx.getServerHandler().player.getUniqueID();
            if (message.recipe == null) {
                handlerMP.removeRecipe(uuid, message.group, message.index);
            } else if (message.index == -1) {
                handlerMP.addRecipe(uuid, message.group, message.recipe);
            } else {
                handlerMP.setRecipe(uuid, message.group, message.groupOld, message.index, message.recipe);
            }
        }
        return null;
    }
}
