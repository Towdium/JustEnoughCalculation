package me.towdium.jecalculation;

import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
    public static JecaItem CRAFT;
    public static JecaItem MATH;

    String key;

    private JecaItem(String name) {
        super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
        setRegistryName("item_calculator_" + name);
        key = "jecalculation.item.calculator_" + name;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        CRAFT = new JecaItem("craft");
        MATH = new JecaItem("math");
        event.getRegistry().registerAll(CRAFT, MATH);
    }

    @Override
    public String getDescriptionId() {
        return key;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack is = playerIn.getItemInHand(handIn);
        if (playerIn.isShiftKeyDown()) {
            ItemStack neu = new ItemStack(is.getItem() == CRAFT ? MATH : CRAFT);
            neu.setTag(is.getTag());
            Inventory inv = playerIn.getInventory();
            if (handIn == InteractionHand.MAIN_HAND) inv.setItem(inv.selected, neu);
            else if (handIn == InteractionHand.OFF_HAND) inv.offhand.set(0, neu);
        } else if (worldIn.isClientSide) {
            if (is.getItem() == CRAFT) JecaGui.openGuiCraft(is);
            else if (is.getItem() == MATH) JecaGui.openGuiMath(is);
            else throw new RuntimeException("Internal error");
        }
        return InteractionResultHolder.success(is);
    }
}
