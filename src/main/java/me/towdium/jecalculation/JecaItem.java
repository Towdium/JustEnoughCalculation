package me.towdium.jecalculation;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiCalculator;
import me.towdium.jecalculation.utils.IllegalPositionException;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber
public class JecaItem extends Item {
    public static JecaItem INSTANCE = new JecaItem();

    private JecaItem() {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.TOOLS);
        setRegistryName("item_calculator");
        setTranslationKey("item_calculator");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(INSTANCE);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == CreativeTabs.SEARCH || tab == CreativeTabs.TOOLS) {
            items.add(new ItemStack(this, 1, 0));
            items.add(new ItemStack(this, 1, 1));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModel(ModelRegistryEvent event) {
        setModelLocation(INSTANCE);
    }

    @SideOnly(Side.CLIENT)
    static void setModelLocation(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(JustEnoughCalculation.Reference.MODID + ":" + id, "inventory"));
    }

    @SideOnly(Side.CLIENT)
    static void setModelLocation(Item item) {
        if (item.getHasSubtypes()) {
            NonNullList<ItemStack> stacks = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, stacks);
            for (ItemStack s : stacks) {
                setModelLocation(item, s.getMetadata(), item.getTranslationKey(s).substring(5));
            }
        } else {
            setModelLocation(item, 0, item.getTranslationKey().substring(5));
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        switch (stack.getMetadata()) {
            case 0:
                return "item.item_calculator_crafting";
            case 1:
                return "item.item_calculator_math";
            default:
                throw new IllegalPositionException();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        JustEnoughCalculation.proxy.runOnSide(() -> JecaGui.displayGui(new GuiCalculator()), Side.CLIENT);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
