package pers.towdium.just_enough_calculation.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.gui.GuiHandler;

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
        playerIn.openGui(JustEnoughCalculation.instance, GuiHandler.GuiId.CALCULATOR, worldIn, 0, 0, 0);
        return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
    }
}