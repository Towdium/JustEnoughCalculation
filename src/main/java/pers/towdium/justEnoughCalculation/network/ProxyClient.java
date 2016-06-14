package pers.towdium.justEnoughCalculation.network;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.event.MouseEventHandler;
import pers.towdium.justEnoughCalculation.gui.GuiHandler;

/**
 * @author Towdium
 */
public class ProxyClient implements IProxy {
    @Override
    public void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(JustEnoughCalculation.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new MouseEventHandler());
    }
}
