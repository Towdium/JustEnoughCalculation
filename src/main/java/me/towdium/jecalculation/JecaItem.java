package me.towdium.jecalculation;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
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
    public static JecaItem INSTANCE = new JecaItem();

    private JecaItem() {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS));
        setRegistryName("item_calculator");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(INSTANCE);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return "jecalculation.item.calculator_crafting";
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack is = playerIn.getHeldItem(handIn);
        //boolean recipe = is.getDamage() == 0;
        //if (playerIn.isSneaking()) is.setDamage(recipe ? 1 : 0);
        //else if (worldIn.isRemote) JecaGui.displayGui(true, true, recipe ? new GuiCraft() : new GuiMath());
        return ActionResult.newResult(ActionResultType.SUCCESS, is);
    }
}
