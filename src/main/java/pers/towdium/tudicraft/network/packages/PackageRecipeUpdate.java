package pers.towdium.tudicraft.network.packages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pers.towdium.tudicraft.Tudicraft;
import pers.towdium.tudicraft.core.Recipe;
import pers.towdium.tudicraft.network.IPlayerHandler;
import pers.towdium.tudicraft.network.PlayerHandlerServer;
import pers.towdium.tudicraft.network.ProxyClient;
import pers.towdium.tudicraft.network.ProxyServer;

import java.util.UUID;

/**
 * @author Towdium
 */
public class PackageRecipeUpdate implements IMessage, IMessageHandler<PackageRecipeUpdate, IMessage> {
    Recipe recipe;
    int index;

    public PackageRecipeUpdate(){}

    public PackageRecipeUpdate(Recipe recipe, int index){
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
    public IMessage onMessage(PackageRecipeUpdate message, MessageContext context) {
        IPlayerHandler playerHandler = Tudicraft.proxy.getPlayerHandler();
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
