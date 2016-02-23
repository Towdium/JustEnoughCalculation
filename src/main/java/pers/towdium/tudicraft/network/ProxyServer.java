package pers.towdium.tudicraft.network;

import net.minecraftforge.common.MinecraftForge;
import pers.towdium.tudicraft.event.PlayerEventHandler;

/**
 * @author Towdium
 */
public class ProxyServer implements IProxy{
    public static PlayerHandlerServer playerHandler = new PlayerHandlerServer();

    @Override
    public IPlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
    }

}
