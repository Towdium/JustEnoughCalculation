package pers.towdium.tudicraft.network.packages;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import pers.towdium.tudicraft.item.ItemCalculator;

/**
 * @author Towdium
 */
public class PackageCalculatorUpdate implements IMessage, IMessageHandler<PackageCalculatorUpdate, IMessage> {
    ItemStack itemStack;

    public PackageCalculatorUpdate(){}

    public PackageCalculatorUpdate(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        itemStack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, itemStack);
    }

    @Override
    public IMessage onMessage(PackageCalculatorUpdate message, MessageContext ctx) {
        if(ctx.getServerHandler().playerEntity.getHeldItem().getItem() instanceof ItemCalculator){
            ctx.getServerHandler().playerEntity.inventory.setInventorySlotContents(
                    ctx.getServerHandler().playerEntity.inventory.currentItem, message.itemStack);
        }
        return null;
    }
}
