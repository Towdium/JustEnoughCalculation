package pers.towdium.just_enough_calculation.network;

import net.minecraftforge.common.MinecraftForge;
import pers.towdium.just_enough_calculation.event.DataEventHandler;

/**
 * @author Towdium
 */
public class ProxyServer implements IProxy {
    static PlayerHandlerMP playerHandler = new PlayerHandlerMP();

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new DataEventHandler());
    }

    @Override
    public void preInit() {
    }

    @Override
    public void postInit() {
    }

    @Override
    public IPlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
