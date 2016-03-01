package pers.towdium.justEnoughCalculation.event;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import net.minecraftforge.event.entity.player.PlayerEvent.*;
import pers.towdium.justEnoughCalculation.gui.guis.calculator.ContainerCalculator;

/**
 * @author Towdium
 */
public class PlayerEventHandler {
    @SubscribeEvent
    public void onLogin(LoadFromFile event){
        JustEnoughCalculation.proxy.getPlayerHandler().handleLogin(event);
    }

    @SubscribeEvent
    public void onSave(SaveToFile event){
        JustEnoughCalculation.proxy.getPlayerHandler().handleSave(event);
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event){
        if(event.entityPlayer.openContainer instanceof ContainerCalculator){
            long l = event.itemStack.hasTagCompound() ? (event.itemStack.getTagCompound().getLong("amount")+99)/100 : 0;
            if(l > 999){
                event.toolTip.add("Amount: " + l);
            }
        }
    }
}
