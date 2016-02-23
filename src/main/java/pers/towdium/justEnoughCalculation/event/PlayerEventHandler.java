package pers.towdium.justEnoughCalculation.event;

import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;

/**
 * @author Towdium
 */
public class PlayerEventHandler {
    @SubscribeEvent
    public void onLogin(net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile event){
        JustEnoughCalculation.proxy.getPlayerHandler().handleLogin(event);
    }

    @SubscribeEvent
    public void onSave(net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile event){
        JustEnoughCalculation.proxy.getPlayerHandler().handleSave(event);
    }
}
