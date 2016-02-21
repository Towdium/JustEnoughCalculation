package pers.towdium.tudicraft.network;

/**
 * @author Towdium
 */
public class ProxyClient implements IProxy {
    PlayerHandlerClient playerHandler = new PlayerHandlerClient();

    @Override
    public IPlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
