package pers.towdium.justEnoughCalculation.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.gui.GuiHandler;


/**
 * @author Towdium
 */
public class ItemCalculator extends Item {

    public ItemCalculator() {
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        playerIn.openGui(JustEnoughCalculation.instance, GuiHandler.GuiId.CALCULATOR, worldIn, 0, 0, 0);
        ItemStack stack = playerIn.inventory.getStackInSlot(0);
        JustEnoughCalculation.log.info(stack==null ? "" : stack.getItem().getUnlocalizedName() + "-" + stack.getItemDamage());
        return itemStackIn;
    }
}