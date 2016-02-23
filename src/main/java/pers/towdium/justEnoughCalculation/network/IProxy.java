package pers.towdium.justEnoughCalculation.network;

/**
 * @author Towdium
 */


public interface IProxy {
    IPlayerHandler getPlayerHandler();

    void init();


}
