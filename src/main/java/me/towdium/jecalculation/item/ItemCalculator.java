package me.towdium.jecalculation.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.polyfill.mc.util.NonNullList;
import me.towdium.jecalculation.utils.IllegalPositionException;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@ParametersAreNonnullByDefault
public class ItemCalculator extends Item {
    public static ItemCalculator INSTANCE = new ItemCalculator();

    public static final String CRAFTING_NAME = "item_calculator_crafting";
    public static final String MATH_NAME = "item_calculator_math";

    private IIcon mathIcon;

    private ItemCalculator() {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName("item_calculator");
        setMaxDamage(1);
    }

    public static void registerItems() {
        GameRegistry.registerItem(INSTANCE, INSTANCE.getUnlocalizedName());
    }


    @Override
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        if (tab == CreativeTabs.tabAllSearch || tab == CreativeTabs.tabTools) {
            items.add(new ItemStack(this, 1, 0));
            items.add(new ItemStack(this, 1, 1));
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        return meta == 1 ? this.mathIcon : this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(JustEnoughCalculation.Reference.MODID + ":" + CRAFTING_NAME);
        this.mathIcon = iconRegister.registerIcon(JustEnoughCalculation.Reference.MODID + ":" + MATH_NAME);
    }

    @SideOnly(Side.CLIENT)
    static void setModelLocation(Item item, int meta, String id) {
        item.setTextureName(JustEnoughCalculation.Reference.MODID + ":" + id);
    }

    @SideOnly(Side.CLIENT)
    static void setModelLocation(Item item) {
        if (item.getHasSubtypes()) {
            NonNullList<ItemStack> stacks = NonNullList.create();
            item.getSubItems(item, CreativeTabs.tabAllSearch, stacks);
            for (ItemStack s : stacks) {
                setModelLocation(item, s.getItemDamage(), item.getUnlocalizedName(s).substring(5));
            }
        } else {
            setModelLocation(item, 0, item.getUnlocalizedName().substring(5));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        switch (stack.getItemDamage()) {
            case 0:
                return "item.item_calculator_crafting";
            case 1:
                return "item.item_calculator_math";
            default:
                throw new IllegalPositionException(String.format("item damage %d", stack.getItemDamage()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World worldIn, EntityPlayer playerIn) {
        if (JustEnoughCalculation.side != JustEnoughCalculation.enumSide.CLIENT)
            JustEnoughCalculation.proxy.displayCalculator();
        return super.onItemRightClick(itemStack, worldIn, playerIn);
    }
}
