package me.towdium.jecalculation.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.GuiHandler;

@SideOnly(Side.CLIENT)
public class ProxyClient implements IProxy {
    @Override
    public void preInit() {

    }

    @Override
    public void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(JustEnoughCalculation.INSTANCE, new GuiHandler());
    }

}
