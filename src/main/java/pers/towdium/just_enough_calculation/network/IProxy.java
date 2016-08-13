package pers.towdium.just_enough_calculation.network;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * @author Towdium
 */

public interface IProxy {
    void init();

    void preInit();

    IPlayerHandler getPlayerHandler();

    interface IPlayerHandler {
        void handleLogin(PlayerEvent.LoadFromFile event);

        void handleSave(PlayerEvent.SaveToFile event);

        void handleJoin(EntityJoinWorldEvent event);
    }
}
