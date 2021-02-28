package me.towdium.jecalculation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.GuiHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


/**
 * @author Towdium
 */
public class JecaItem extends Item {
    public static JecaItem INSTANCE = new JecaItem();

    public JecaItem() {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName("item_calculator");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        playerIn.openGui(JustEnoughCalculation.instance, GuiHandler.GuiId.CALCULATOR, worldIn, 0, 0, 0);
        ItemStack stack = playerIn.inventory.getStackInSlot(0);
        //JustEnoughCalculation.log.info(stack==null ? "" : stack.getItem().getUnlocalizedName() + "-" + stack.getItemDamage());
        return itemStackIn;
    }
}