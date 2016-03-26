package pers.towdium.justEnoughCalculation.plugin;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.IRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.core.ItemStackWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Towdium on 2016/3/21.
 */
public class MyOverlayHandler implements IOverlayHandler {
    @Override
    public void overlayRecipe(GuiContainer guiContainer, IRecipeHandler iRecipeHandler, int i, boolean b) {
        ItemStack buffer = iRecipeHandler.getResultStack(i).item.copy();
        ItemStackWrapper.NBT.setBool(buffer, JustEnoughCalculation.Reference.MODID, true);
        guiContainer.inventorySlots.getSlot(0).putStack(buffer);
        List<ItemStack> itemStacks = new ArrayList<>();
        LOOP:
        for(PositionedStack item : iRecipeHandler.getIngredientStacks(i)){
            for(ItemStack current : itemStacks){
                if(ItemStackWrapper.isTypeEqual(current, item.items[0])){
                    current.stackSize++;
                    continue LOOP;
                }
            }
            itemStacks.add(item.items[0].copy());
        }
        int index=4;
        for(ItemStack itemStack : itemStacks){
            ItemStackWrapper.NBT.setBool(itemStack, JustEnoughCalculation.Reference.MODID, true);
            guiContainer.inventorySlots.getSlot(index++).putStack(itemStack);
        }
    }
}
