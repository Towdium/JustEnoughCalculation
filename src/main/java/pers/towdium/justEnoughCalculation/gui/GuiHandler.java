package pers.towdium.justEnoughCalculation.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.gui.guis.calculator.ContainerCalculator;
import pers.towdium.justEnoughCalculation.gui.guis.calculator.GuiCalculator;
import pers.towdium.justEnoughCalculation.gui.commom.recipe.ContainerRecipe;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.ContainerRecipeEditor;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.GuiRecipeEditor;
import pers.towdium.justEnoughCalculation.gui.guis.recipePicker.GuiRecipePicker;
import pers.towdium.justEnoughCalculation.gui.guis.recipeViewer.ContainerRecipeViewer;
import pers.towdium.justEnoughCalculation.gui.guis.recipeViewer.GuiRecipeViewer;


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
