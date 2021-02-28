package me.towdium.jecalculation.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.guis.calculator.ContainerCalculator;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.*;

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
