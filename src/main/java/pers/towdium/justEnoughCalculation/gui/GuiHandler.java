package pers.towdium.justEnoughCalculation.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import pers.towdium.justEnoughCalculation.gui.guis.calculator.ContainerCalculator;
import pers.towdium.justEnoughCalculation.gui.guis.calculator.GuiCalculator;
import pers.towdium.justEnoughCalculation.gui.guis.itemPicker.ContainerItemPicker;
import pers.towdium.justEnoughCalculation.gui.guis.itemPicker.GuiItemPicker;
import pers.towdium.justEnoughCalculation.gui.commom.recipe.ContainerRecipe;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.ContainerRecipeEditor;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.GuiRecipeEditor;
import pers.towdium.justEnoughCalculation.gui.guis.recipePicker.GuiRecipePicker;


/**
 * @author Towdium
 */
public class GuiHandler implements IGuiHandler {
    public static final class GuiId{
        public static final int CALCULATOR = 0;
        public static final int ITEM_PICKER = 1;
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
            case GuiId.ITEM_PICKER: return new GuiItemPicker(new ContainerItemPicker());
            case GuiId.RECIPE_EDITOR: return new GuiRecipeEditor(new ContainerRecipeEditor());
            case GuiId.RECIPE_PICKER: return new GuiRecipePicker(new ContainerRecipe());
        }
        return null;
    }
}
