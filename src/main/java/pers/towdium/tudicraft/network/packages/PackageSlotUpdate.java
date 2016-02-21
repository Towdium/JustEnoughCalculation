package pers.towdium.tudicraft.network.packages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author Towdium
 */
public class PackageSlotUpdate implements IMessage, IMessageHandler<PackageSlotUpdate, IMessage> {
    int slot;
    int playerId;
    ItemStack itemStack=null;

    public PackageSlotUpdate(){}


    public PackageSlotUpdate(EntityPlayer player, int index){
        playerId = player.getEntityId();
        slot = index;
        this.itemStack = player.inventory.getStackInSlot(index);
    }



    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(slot);
        buffer.writeInt(playerId);
        ByteBufUtils.writeItemStack(buffer, itemStack);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        slot = buffer.readByte();
        playerId = buffer.readInt();
        itemStack = ByteBufUtils.readItemStack(buffer);
    }




    @Override
    public IMessage onMessage(PackageSlotUpdate message, MessageContext context) {
        context.getServerHandler().playerEntity.inventory.setInventorySlotContents(0, message.itemStack);
//        Entity player = FMLServerHandler.instance().getServer().getEntityWorld().getEntityByID(playerId);
//        System.out.println("get entity");
//
//        if(player instanceof EntityPlayerMP){
//            System.out.println("get entityMP!");
//            ((EntityPlayerMP) player).getInventory()[0] = new ItemStack(new ItemCalculator(),10);
//        }
        return null;
    }

}
