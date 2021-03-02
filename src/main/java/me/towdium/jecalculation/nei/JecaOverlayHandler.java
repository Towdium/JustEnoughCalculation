package me.towdium.jecalculation.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.gui.guis.GuiEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JecaOverlayHandler implements IOverlayHandler {
    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        if(firstGui instanceof GuiEditor) {
            List<ItemStack> inputStacks = new ArrayList<>();
            LOOP:
            for (PositionedStack positionedStack : recipe.getIngredientStacks(recipeIndex)) {
                ItemStack itemStack = positionedStack.items[0];
                for (ItemStack exist : inputStacks) {
                    if (ItemStackHelper.isTypeEqual(exist, itemStack)) {
                        exist.stackSize += itemStack.stackSize;
                        continue LOOP;
                    }
                }
                inputStacks.add(itemStack.copy());
            }
            ItemStack outputStack = recipe.getResultStack(recipeIndex).items[0];

            Minecraft mc = Minecraft.getMinecraft();
            mc.displayGuiScreen(new GuiEditor(firstGui));
            GuiContainer myGuiContainer = (GuiContainer) mc.currentScreen;
            if (myGuiContainer != null) {
                myGuiContainer.inventorySlots.getSlot(0).putStack(outputStack);
                for (int i = 8; i <= 8 + inputStacks.size() - 1 && i <= 19; i++) {
                    myGuiContainer.inventorySlots.getSlot(i).putStack(inputStacks.get(i - 8));
                }
                //crafting item, nei may not have
                //                myGuiContainer.inventorySlots.getSlot(4).putStack();
            }

        } else {
            System.out.println(firstGui.getClass().toString());
        }

    }
}
