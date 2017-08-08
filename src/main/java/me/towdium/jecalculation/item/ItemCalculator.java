package me.towdium.jecalculation.item;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.network.packets.PacketSyncCalculator;
import me.towdium.jecalculation.util.Utilities;
import me.towdium.jecalculation.util.exception.IllegalPositionException;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author Towdium
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemCalculator extends Item {

    public ItemCalculator() {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.MISC);
        setRegistryName("item_calculator");
        setUnlocalizedName("item_calculator");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStackIn = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) { // client
            if ((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
                itemStackIn = new ItemStack(itemStackIn.getItem(), itemStackIn.getCount(), Utilities.circulate(itemStackIn.getMetadata(), 2, true), itemStackIn.getTagCompound());
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

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        switch (stack.getMetadata()) {
            case 0:
                return "item.item_calculator";
            case 1:
                return "item.item_math_calculator";
            default:
                throw new IllegalPositionException();
        }
    }

    @Override
    public boolean getHasSubtypes() {
        return super.getHasSubtypes();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }
}