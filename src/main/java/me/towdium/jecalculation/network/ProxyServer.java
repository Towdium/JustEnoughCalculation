package me.towdium.jecalculation.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.event.Handlers;
import me.towdium.jecalculation.item.ItemCalculator;
import net.minecraftforge.common.MinecraftForge;

public class ProxyServer implements IProxy {
    @Override
    public void initPre() {
        GameRegistry.registerItem(ItemCalculator.INSTANCE, ItemCalculator.INSTANCE.getUnlocalizedName());
        Handlers.handlers.forEach(FMLCommonHandler.instance().bus()::register);
        MinecraftForge.EVENT_BUS.register(JecGui.class);
    }

    @Override
    public void initPost() {
        ILabel.initServer();
    }
}
