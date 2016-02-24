package pers.towdium.justEnoughCalculation.gui.guis.recipePicker;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.core.Recipe;
import pers.towdium.justEnoughCalculation.gui.commom.recipe.ContainerRecipe;
import pers.towdium.justEnoughCalculation.gui.commom.recipe.GuiRecipe;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.ContainerRecipeEditor;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.GuiRecipeEditor;
import java.io.IOException;
import java.util.List;

/**
 * @author Towdium
 */
public class GuiRecipePicker extends GuiRecipe{
    ItemStack dest;
    List<Recipe> recipes;
    int index;

    public GuiRecipePicker(ContainerRecipe containerRecipe, GuiScreen parent){
        super(containerRecipe, parent);
    }

    public GuiRecipePicker(ContainerRecipe containerRecipe){
        super(containerRecipe);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(16, guiLeft+125, guiTop+7, 20, 20, StatCollector.translateToLocal("<")));
        buttonList.add(new GuiButton(17, guiLeft+149, guiTop+7, 20, 20, StatCollector.translateToLocal(">")));
        buttonList.add(new GuiButton(18, guiLeft+125, guiTop+31, 44, 20, StatCollector.translateToLocal("gui.recipePicker.delete")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        int buttonId = button.id;
        if(buttonId == 16) {
            if(index>0){
                index--;
                updateLayout();
            }
        }else if(buttonId == 17) {
            if(index<recipes.size()-1){
                index++;
                updateLayout();
            }
        }else if(buttonId == 18) {
            JustEnoughCalculation.proxy.getPlayerHandler().removeRecipe(recipes.get(index), null);
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        Slot slot = getSlotUnderMouse();
        if(slot != null){
            GuiRecipeEditor recipeEditor = new GuiRecipeEditor(new ContainerRecipeEditor(), parent);
            mc.displayGuiScreen(recipeEditor);
            recipeEditor.click(mouseX, mouseY, mouseButton);
            recipeEditor.setActiveSlot(slot.getSlotIndex());
        }
    }

    public void updateLayout(){

    }
}
