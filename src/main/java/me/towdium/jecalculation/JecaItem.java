package me.towdium.jecalculation;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.Controller;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = MOD)
public class JecaItem extends Item {
    public static JecaItem CRAFT = new JecaItem("craft");
    public static JecaItem MATH = new JecaItem("math");

    String key;

    private JecaItem(String name) {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS));
        setRegistryName("item_calculator_" + name);
        key = "jecalculation.item.calculator_" + name;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(CRAFT, MATH);
    }

    @Override
    public String getTranslationKey() {
        return key;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack is = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            ItemStack neu = new ItemStack(is.getItem() == CRAFT ? MATH : CRAFT);
            neu.setTag(is.getTag());
            PlayerInventory inv = playerIn.inventory;
            if (handIn == Hand.MAIN_HAND) inv.setInventorySlotContents(inv.currentItem, neu);
            else if (handIn == Hand.OFF_HAND) inv.offHandInventory.set(0, neu);
        } else if (worldIn.isRemote) {
            if (is.getItem() == CRAFT) Controller.openGuiCraft();
            else if (is.getItem() == MATH) Controller.openGuiMath();
            else throw new RuntimeException("Internal error");
        }
        return ActionResult.newResult(ActionResultType.SUCCESS, is);
    }
}
