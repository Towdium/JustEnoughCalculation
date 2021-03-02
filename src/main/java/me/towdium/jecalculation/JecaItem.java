package me.towdium.jecalculation;

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

    public static final String NAME = "itemCalculator";

    public JecaItem() {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setUnlocalizedName(NAME);
        setTextureName(JustEnoughCalculation.Reference.MODID + ":" + NAME);
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        playerIn.openGui(JustEnoughCalculation.INSTANCE, GuiHandler.GuiId.EDITOR, worldIn, 0, 0, 0);
        return itemStackIn;
    }
}