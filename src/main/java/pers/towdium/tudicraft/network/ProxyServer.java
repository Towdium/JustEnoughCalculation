package pers.towdium.tudicraft.network;

/**
 * @author Towdium
 */
public class ProxyServer implements IProxy{
    PlayerHandlerServer playerHandler = new PlayerHandlerServer();

    @Override
    public IPlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
