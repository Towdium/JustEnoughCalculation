package pers.towdium.justEnoughCalculation.plugin;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.RecipesGui;
import mezz.jei.gui.ingredients.IGuiIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import pers.towdium.justEnoughCalculation.util.ItemStackHelper;
import pers.towdium.justEnoughCalculation.gui.guis.GuiEditor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Towdium
 */
public class JECRecipeTransferHandler implements IRecipeTransferHandler {
    String recipeUID;
    Class<? extends Container> container;

    public JECRecipeTransferHandler(String recipeUID, Class<? extends Container> container) {
        this.recipeUID = recipeUID;
        this.container = container;
    }

    @Override
    public Class<? extends Container> getContainerClass() {
        return container;
    }

    @Override
    public String getRecipeCategoryUid() {
        return recipeUID;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(@Nonnull Container container, @Nonnull IRecipeLayout iRecipeLayout, @Nonnull EntityPlayer entityPlayer, boolean maxTransfer, boolean doTransfer) {
        if(doTransfer){
            List<ItemStack> outputStacks = new ArrayList<>();
            List<ItemStack> inputStacks = new ArrayList<>();
            LOOP:
            for(IGuiIngredient<ItemStack> ingredient : iRecipeLayout.getItemStacks().getGuiIngredients().values()){
                if(ingredient.getAllIngredients().size()==0){
                    continue;
                }
                if(ingredient.isInput()){
                    ItemStack itemStack = ingredient.getAllIngredients().get(0);
                    for (ItemStack exist : inputStacks){
                        if(ItemStackHelper.isTypeEqual(exist, itemStack)){
                            exist.stackSize += itemStack.stackSize;
                            continue LOOP;
                        }
                    }
                    inputStacks.add(itemStack.copy());
                }else {
                    ItemStack itemStack = ingredient.getAllIngredients().get(0);
                    for (ItemStack exist : outputStacks){
                        if(ItemStackHelper.isTypeEqual(exist, itemStack)){
                            exist.stackSize += itemStack.stackSize;
                            continue LOOP;
                        }
                    }
                    outputStacks.add(itemStack.copy());
                }
            }
            Minecraft mc = Minecraft.getMinecraft();
            RecipesGui gui = (RecipesGui) mc.currentScreen;
            if(gui!=null){
                mc.displayGuiScreen(new GuiEditor(gui.getParentScreen()));
                GuiContainer myGuiContainer = (GuiContainer) mc.currentScreen;
                if(myGuiContainer != null){
                    for(int i=0; i<=outputStacks.size()-1 && i<=3; i++){
                        myGuiContainer.inventorySlots.getSlot(i).putStack(outputStacks.get(i));
                    }
                    for(int i=8; i<=8+inputStacks.size()-1 && i<=19; i++){
                        myGuiContainer.inventorySlots.getSlot(i).putStack(inputStacks.get(i-8));
                    }
                    myGuiContainer.inventorySlots.getSlot(4).putStack(
                            JEIPlugin.recipeRegistry.getCraftingItems(
                                    JEIPlugin.recipeRegistry.getRecipeCategories(Collections.singletonList(recipeUID)).get(0)
                            ).iterator().next().copy()
                    );
                }
            }
        }
        return null;
    }
}
