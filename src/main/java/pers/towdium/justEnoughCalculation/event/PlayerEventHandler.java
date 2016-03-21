package pers.towdium.justEnoughCalculation.event;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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
        if(!JustEnoughCalculation.JECConfig.initialized){
            ArrayList<String> idents = new ArrayList<>();
            LOOP:
            for(ICraftingHandler handler : GuiCraftingRecipe.craftinghandlers){
                if(handler instanceof TemplateRecipeHandler){
                    if(((TemplateRecipeHandler) handler).getOverlayIdentifier() == null){
                        continue;
                    }
                    for (String id : idents){
                        if(((TemplateRecipeHandler) handler).getOverlayIdentifier().equals(id) ){
                            continue LOOP;
                        }
                    }
                    idents.add(((TemplateRecipeHandler) handler).getOverlayIdentifier());
                }
            }
            String[] strings = new String[idents.size()];
            for(int i=0; i<idents.size(); i++){
                strings[i] = idents.get(i);
            }
            JustEnoughCalculation.JECConfig.EnumItems.ListRecipeCategory.getProperty().set(strings);
            JustEnoughCalculation.JECConfig.save();
            JustEnoughCalculation.JECConfig.initialized = true;
        }
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
