package me.towdium.jecalculation.network;

import me.towdium.jecalculation.event.DataEventHandler;
import me.towdium.jecalculation.event.RegisterEventHandler;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author Towdium
 */
public class ProxyServer implements IProxy {
    static PlayerHandlerMP playerHandler = new PlayerHandlerMP();

    @Override
    public void init() {

    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new DataEventHandler());
        MinecraftForge.EVENT_BUS.register(new RegisterEventHandler());
    }

    @Override
    public void postInit() {
    }

    @Override
    public IPlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
