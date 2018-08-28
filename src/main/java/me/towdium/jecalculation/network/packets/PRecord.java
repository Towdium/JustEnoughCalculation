package me.towdium.jecalculation.network.packets;

import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.structure.Recipes;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;

public class PRecord implements IMessage {
    public static final String KEY = "content";
    NBTTagList recipes;

    public PRecord(Recipes recipes) {
        this.recipes = recipes.serialize();
    }

    public PRecord() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = Objects.requireNonNull(ByteBufUtils.readTag(buf));
        recipes = tag.getTagList(KEY, 10);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(KEY, recipes);
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler implements IMessageHandler<PRecord, IMessage> {
        @Override
        public IMessage onMessage(PRecord message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = JustEnoughCalculation.proxy.getPlayer();
                if (player != null) {
                    Recipes recipes = Utilities.getRecipes(player);
                    recipes.deserialize(message.recipes);
                }
            });
            return null;
        }
    }
}
