package me.towdium.jecalculation.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.event.Handlers;
import me.towdium.jecalculation.item.ItemCalculator;

public class ProxyServer implements IProxy {
    public void initPre() {
        GameRegistry.registerItem(ItemCalculator.INSTANCE, ItemCalculator.INSTANCE.getUnlocalizedName());
        Handlers.handlers.forEach(FMLCommonHandler.instance().bus()::register);
    }
}
