package me.towdium.jecalculation.network;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * @author Towdium
 */

public interface IProxy {
    void init();

    void preInit();

    void postInit();

    IPlayerHandler getPlayerHandler();

    interface IPlayerHandler {
        void handleLogin(PlayerEvent.LoadFromFile event);

        void handleSave(PlayerEvent.SaveToFile event);

        void handleJoin(EntityJoinWorldEvent event);
    }
}
