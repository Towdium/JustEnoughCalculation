package pers.towdium.justEnoughCalculation.plugin;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.ingredients.IGuiIngredient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import pers.towdium.justEnoughCalculation.core.ItemStackWrapper;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.ContainerRecipeEditor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Towdium
 */
public class NormalRecipeTransferHandler implements IRecipeTransferHandler {
    String recipeUID;

    public NormalRecipeTransferHandler(String recipeUID ) {
        this.recipeUID = recipeUID;
    }

    @Override
    public Class<? extends Container> getContainerClass() {
        return ContainerRecipeEditor.class;
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
                        if(ItemStackWrapper.isTypeEqual(exist, itemStack)){
                            exist.stackSize += itemStack.stackSize;
                            continue LOOP;
                        }
                    }
                    inputStacks.add(itemStack.copy());
                }else {
                    ItemStack itemStack = ingredient.getAllIngredients().get(0);
                    for (ItemStack exist : outputStacks){
                        if(ItemStackWrapper.isTypeEqual(exist, itemStack)){
                            exist.stackSize += itemStack.stackSize;
                            continue LOOP;
                        }
                    }
                    outputStacks.add(itemStack.copy());
                }
            }
            for(int i=0; i<=outputStacks.size()-1 && i<=3; i++){
                container.getSlot(i).putStack(outputStacks.get(i));
            }
            for(int i=4; i<=4+inputStacks.size()-1 && i<=15; i++){
                container.getSlot(i).putStack(inputStacks.get(i-4));
            }
        }
        return null;
    }
}
