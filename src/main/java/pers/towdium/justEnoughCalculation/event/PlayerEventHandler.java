package pers.towdium.justEnoughCalculation.event;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import net.minecraftforge.event.entity.player.PlayerEvent.*;
import pers.towdium.justEnoughCalculation.gui.guis.calculator.ContainerCalculator;

import java.util.ArrayList;
import java.util.List;

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
