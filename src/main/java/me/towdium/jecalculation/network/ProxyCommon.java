package me.towdium.jecalculation.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.event.Handlers;
import me.towdium.jecalculation.item.JecaItem;
import net.minecraftforge.common.MinecraftForge;

public class ProxyCommon {
    public void initPre() {
        JustEnoughCalculation.logger.info("common proxy init pre");
        GameRegistry.registerItem(JecaItem.INSTANCE, JecaItem.CRAFTING_NAME);
        Handlers.handlers.forEach(FMLCommonHandler.instance().bus()::register);
    }

    public void init() {
        JustEnoughCalculation.logger.info("common proxy init");
    }

    public void initPost() {
        JustEnoughCalculation.logger.info("common proxy init post");
    }
}
