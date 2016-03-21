package pers.towdium.justEnoughCalculation.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.core.Recipe;
import pers.towdium.justEnoughCalculation.network.IPlayerHandler;
import pers.towdium.justEnoughCalculation.network.PlayerHandlerServer;

import java.util.UUID;

/**
 * @author Towdium
 */
public class PacketRecipeUpdate implements IMessage, IMessageHandler<PacketRecipeUpdate, IMessage> {
    Recipe recipe;
    int index;

    public PacketRecipeUpdate(){}

    public PacketRecipeUpdate(Recipe recipe, int index){
        this.recipe = recipe;
        this.index = index;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(index);
        Recipe.ByteBufUtl.toByte(buffer, recipe);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        index = buffer.readInt();
        recipe = Recipe.ByteBufUtl.fromByte(buffer);
    }

    @Override
    public IMessage onMessage(PacketRecipeUpdate message, MessageContext context) {
        IPlayerHandler playerHandler = JustEnoughCalculation.proxy.getPlayerHandler();
        if(playerHandler instanceof PlayerHandlerServer){
            UUID uuid = context.getServerHandler().playerEntity.getUniqueID();
            if(message.index == -1){
                playerHandler.addRecipe(message.recipe, uuid);
            }else if (message.recipe != null){
                playerHandler.setRecipe(message.recipe, message.index, uuid);
            }else {
                playerHandler.removeRecipe(index, uuid);
            }
        }
        return null;
    }

}
