package me.towdium.jecalculation.event;

import com.google.common.base.CaseFormat;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Author: towdium
 * Date:   8/8/17.
 */

public class RegisterEventHandler {

    static void setModelLocation(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(JustEnoughCalculation.Reference.MODID + ":" +
                        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, id), "inventory"));
        JustEnoughCalculation.log.info("Register model");
    }

    static void setModelLocation(Item item) {
        setModelLocation(item, 0, item.getUnlocalizedName().substring(5));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                JustEnoughCalculation.itemCalculator,
                JustEnoughCalculation.itemFluidContainer,
                JustEnoughCalculation.itemLabel
        );
    }

    @SubscribeEvent
    public void registerModel(ModelRegistryEvent event) {
        setModelLocation(JustEnoughCalculation.itemCalculator);
        setModelLocation(JustEnoughCalculation.itemCalculator, 1, "item_math_calculator");
        setModelLocation(JustEnoughCalculation.itemFluidContainer);
        setModelLocation(JustEnoughCalculation.itemLabel);
    }
}
