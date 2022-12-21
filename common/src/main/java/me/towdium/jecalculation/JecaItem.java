package me.towdium.jecalculation;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JecaItem extends Item {

    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(JustEnoughCalculation.MODID, Registries.ITEM);

    public static RegistrySupplier<JecaItem> CRAFT = register("craft");
    public static RegistrySupplier<JecaItem> MATH = register("math");

    String key;

    private JecaItem(String name) {
        super(new Item.Properties().stacksTo(1).arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES));
        key = "jecalculation.item.calculator_" + name;
    }

    public static void register() {
        REGISTRY.register();
    }

    private static RegistrySupplier<JecaItem> register(String name) {
        return REGISTRY.register(new ResourceLocation(JustEnoughCalculation.MODID, "item_calculator_" + name), () -> new JecaItem(name));
    }

    @Override
    public String getDescriptionId() {
        return key;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack is = playerIn.getItemInHand(handIn);
        Inventory inv = playerIn.getInventory();
        if (playerIn.isShiftKeyDown()) {
            ItemStack neu = new ItemStack(is.getItem() == CRAFT.get() ? MATH.get() : CRAFT.get());
            neu.setTag(is.getTag());
            if (handIn == InteractionHand.MAIN_HAND) inv.setItem(inv.selected, neu);
            else if (handIn == InteractionHand.OFF_HAND) inv.offhand.set(0, neu);
        } else if (worldIn.isClientSide) {
            if (is.getItem() == CRAFT.get()) JecaGui.openGuiCraft(is, inv.selected);
            else if (is.getItem() == MATH.get()) JecaGui.openGuiMath(is, inv.selected);
            else throw new RuntimeException("Internal error");
        }
        return InteractionResultHolder.success(is);
    }
}
