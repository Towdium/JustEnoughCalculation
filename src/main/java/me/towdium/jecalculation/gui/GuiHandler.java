package me.towdium.jecalculation.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import me.towdium.jecalculation.gui.guis.calculator.ContainerCalculator;
import me.towdium.jecalculation.gui.guis.calculator.GuiCalculator;
import me.towdium.jecalculation.gui.commom.recipe.ContainerRecipe;
import me.towdium.jecalculation.gui.guis.recipeEditor.ContainerRecipeEditor;
import me.towdium.jecalculation.gui.guis.recipeEditor.GuiRecipeEditor;
import me.towdium.jecalculation.gui.guis.recipePicker.GuiRecipePicker;
import me.towdium.jecalculation.gui.guis.recipeViewer.ContainerRecipeViewer;
import me.towdium.jecalculation.gui.guis.recipeViewer.GuiRecipeViewer;


/**
 * @author Towdium
 */
public class GuiHandler implements IGuiHandler {
    public static final class GuiId{
        public static final int CALCULATOR = 0;
        public static final int RECIPE_VIEWER = 1;
        public static final int RECIPE_EDITOR = 2;
        public static final int RECIPE_PICKER = 3;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GuiId.CALCULATOR: return new GuiCalculator(new ContainerCalculator(player, player.getHeldItem()));
            case GuiId.RECIPE_VIEWER: return new GuiRecipeViewer(new ContainerRecipeViewer(), null);
            case GuiId.RECIPE_EDITOR: return new GuiRecipeEditor(new ContainerRecipeEditor(), null);
            case GuiId.RECIPE_PICKER: return new GuiRecipePicker(new ContainerRecipe(), null, JustEnoughCalculation.proxy.getPlayerHandler().getAllRecipeIndex(null));
        }
        return null;
    }
}
