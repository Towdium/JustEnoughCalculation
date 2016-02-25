package pers.towdium.justEnoughCalculation.event;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.gui.commom.recipe.ContainerRecipe;

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

    @SubscribeEvent
    public void onTooptipRender(ItemTooltipEvent event){
        if(((EntityPlayerSP) event.entityPlayer).openContainer instanceof ContainerRecipe){
            if(event.itemStack.hasTagCompound()){
                event.toolTip.add(event.itemStack.getTagCompound().getInteger("percentage") + "%");
            }
        }
    }
}
