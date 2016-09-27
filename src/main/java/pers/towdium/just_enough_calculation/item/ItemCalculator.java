package pers.towdium.just_enough_calculation.item;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.gui.JECGuiHandler;
import pers.towdium.just_enough_calculation.network.PlayerHandlerSP;
import pers.towdium.just_enough_calculation.network.packets.PacketSyncCalculator;
import pers.towdium.just_enough_calculation.util.Utilities;
import pers.towdium.just_enough_calculation.util.exception.IllegalPositionException;

/**
 * @author Towdium
 */
public class ItemCalculator extends Item {

    public ItemCalculator() {
        setMaxStackSize(1);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if(playerIn instanceof EntityPlayerSP) {
            if((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))){
                Utilities.setField(itemStackIn, Utilities.circulate(itemStackIn.getMetadata(), 2, true), "field_77991_e", "itemDamage");
                JustEnoughCalculation.networkWrapper.sendToServer(new PacketSyncCalculator(itemStackIn));
            } else {
                playerIn.openGui(JustEnoughCalculation.instance, itemStackIn.getMetadata(), worldIn, 0, 0, 0);
            }
        } else {
            playerIn.openGui(JustEnoughCalculation.instance, itemStackIn.getMetadata(), worldIn, 0, 0, 0);
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        switch (stack.getMetadata()) {
            case 0: return "item.itemCalculator";
            case 1: return "item.itemMathCalculator";
            default: throw new IllegalPositionException();
        }
    }
}