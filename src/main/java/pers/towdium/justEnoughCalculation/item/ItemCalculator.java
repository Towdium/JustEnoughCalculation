package pers.towdium.justEnoughCalculation.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.gui.GuiHandler;

import java.util.List;

/**
 * @author Towdium
 */
public class ItemCalculator extends Item {

    public ItemCalculator() {
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        playerIn.openGui(JustEnoughCalculation.instance, GuiHandler.GuiId.RECIPE_PICKER, worldIn, 0, 0, 0);
        return itemStackIn;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        //super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add("sdji");
    }
}
