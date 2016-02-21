package pers.towdium.tudicraft.network.packages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import pers.towdium.tudicraft.core.Recipe;

/**
 * @author Towdium
 */
public class PackageRecipeUpdate implements IMessage, IMessageHandler<PackageRecipeUpdate, IMessage> {
    Recipe recipe;

    public PackageRecipeUpdate(){}


    public PackageRecipeUpdate(Recipe recipe){
        this.recipe = recipe;
    }

    @Override
    public void toBytes(ByteBuf buffer) {

    }

    @Override
    public void fromBytes(ByteBuf buffer) {

    }




    @Override
    public IMessage onMessage(PackageRecipeUpdate message, MessageContext context) {

        return null;
    }

}
