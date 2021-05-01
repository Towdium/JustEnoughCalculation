package me.towdium.jecalculation.nei;

import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiRecipe;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public class JecaOverlayHandler implements IOverlayHandler {
    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        JustEnoughCalculation.logger.info("shift ? " + shift);
        if (shift && firstGui instanceof JecaGui) {
            JecaGui gui = (JecaGui) firstGui;
            if (gui.root instanceof GuiRecipe) {
                ((GuiRecipe) gui.root).transfer(recipe, recipeIndex);
            } else {
                GuiRecipe guiRecipe = new GuiRecipe();
                JecaGui.displayGui(true, true, guiRecipe);
                guiRecipe.transfer(recipe, recipeIndex);
            }
        } else {
            System.out.println(firstGui.getClass().toString());
        }

    }
}
