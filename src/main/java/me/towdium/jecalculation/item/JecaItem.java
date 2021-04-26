package me.towdium.jecalculation.item;

import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


/**
 * @author Towdium
 */
public class JecaItem extends Item {
    public static JecaItem INSTANCE = new JecaItem();

    public static final String CRAFTING_NAME = "item_calculator_crafting";
    public static final String MATH_NAME = "item_calculator_math";


    public JecaItem() {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setUnlocalizedName(CRAFTING_NAME);
        setTextureName(JustEnoughCalculation.Reference.MODID + ":" + CRAFTING_NAME);
        setCreativeTab(CreativeTabs.tabTools);
    }

//    @Override
//    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
//        playerIn.openGui(JustEnoughCalculation.INSTANCE, GuiHandler.GuiId.EDITOR, worldIn, 0, 0, 0);
//        return itemStackIn;
//    }

    public String getUnlocalizedName(int meta) {
        return getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName(stack.getItemDamage());
    }
}