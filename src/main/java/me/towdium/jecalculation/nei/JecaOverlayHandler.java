package me.towdium.jecalculation.nei;

import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.guis.GuiRecipe;
import net.minecraft.client.gui.inventory.GuiContainer;

public class JecaOverlayHandler implements IOverlayHandler {
    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        JustEnoughCalculation.logger.info("shift ? " + shift);
        if (shift && firstGui instanceof JecGui) {
            JecGui gui = (JecGui) firstGui;
            if (gui.root instanceof GuiRecipe) {
                ((GuiRecipe) gui.root).transfer(recipe, recipeIndex);
            }
        } else {
            System.out.println(firstGui.getClass().toString());
        }

    }
}
