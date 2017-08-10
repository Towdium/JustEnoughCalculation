package me.towdium.jecalculation.event.handlers;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.item.Items;
import me.towdium.jecalculation.item.JecItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RegisterEventHandler {
    static void setModelLocation(JecItem item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(JustEnoughCalculation.Reference.MODID + ":" + id, "inventory"));
    }

    static void setModelLocation(JecItem item) {
        if (item.getHasSubtypes()) {
            NonNullList<ItemStack> stacks = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, stacks);
            for (ItemStack s : stacks) {
                setModelLocation(item, s.getMetadata(), item.getUnlocalizedName(s).substring(5));
            }
        } else {
            setModelLocation(item, 0, item.getUnlocalizedName().substring(5));
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        Items.items.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public void registerModel(ModelRegistryEvent event) {
        Items.items.forEach(RegisterEventHandler::setModelLocation);
    }
}
