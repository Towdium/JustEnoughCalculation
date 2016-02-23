package pers.towdium.tudicraft.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.towdium.tudicraft.Tudicraft;

import java.util.UUID;

/**
 * @author Towdium
 */
public class PlayerEventHandler {
    @SubscribeEvent
    public void onLogin(net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile event){
        Tudicraft.proxy.getPlayerHandler().handleLogin(event);
    }

    @SubscribeEvent
    public void onSave(net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile event){
        Tudicraft.proxy.getPlayerHandler().handleSave(event);
    }
}
