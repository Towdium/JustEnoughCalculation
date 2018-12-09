package me.towdium.jecalculation.network.packets;

import io.netty.buffer.ByteBuf;
import me.towdium.jecalculation.JecaCapability;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.structure.Recipes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;

public class PRecord implements IMessage {
    public static final String KEY = "content";
    NBTTagCompound recipes;

    public PRecord(Recipes recipes) {
        this.recipes = recipes.serialize();
    }

    public PRecord() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = Objects.requireNonNull(ByteBufUtils.readTag(buf));
        recipes = tag.getCompoundTag(KEY);
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
                    Recipes recipes = JecaCapability.getRecipes(player);
                    recipes.deserialize(message.recipes);
                }
            });
            Controller.setServerActive(true);
            return null;
        }
    }
}