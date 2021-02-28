package me.towdium.jecalculation.gui.guis.recipePicker;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.commom.recipe.ContainerRecipe;
import me.towdium.jecalculation.gui.commom.recipe.GuiRecipe;
import me.towdium.jecalculation.gui.guis.recipeEditor.ContainerRecipeEditor;
import me.towdium.jecalculation.gui.guis.recipeEditor.GuiRecipeEditor;
import me.towdium.jecalculation.network.packets.PacketRecipeUpdate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

import java.util.List;

/**
 * @author Towdium
 */
public class GuiRecipePicker extends GuiRecipe {
    protected List<Integer> recipes;
    protected int index;
    protected GuiButton buttonLeft;
    protected GuiButton buttonRight;

    public GuiRecipePicker(ContainerRecipe containerRecipe, GuiScreen parent, List<Integer> recipes) {
        super(containerRecipe, parent);
        this.recipes = recipes;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonLeft = new GuiButton(16, guiLeft + 125, guiTop + 7, 20, 20, "<");
        buttonRight = new GuiButton(17, guiLeft + 149, guiTop + 7, 20, 20, ">");
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        buttonList.add(new GuiButton(18, guiLeft + 125, guiTop + 31, 44, 20,
                                     StatCollector.translateToLocal("gui.recipePicker.delete")));
        updateLayout();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        int buttonId = button.id;
        if (buttonId >= 0 && buttonId <= 15) {
            GuiRecipeEditor recipeEditor = new GuiRecipeEditor(new ContainerRecipeEditor(), parent, recipes.get(index));
            mc.displayGuiScreen(recipeEditor);
            recipeEditor
                    .displayRecipe(JustEnoughCalculation.proxy.getPlayerHandler().getRecipe(recipes.get(index), null));
            recipeEditor.click(buttonId);
        } else if (buttonId == 16) {
            if (index > 0) {
                index--;
                updateLayout();
            }
        } else if (buttonId == 17) {
            if (index < recipes.size() - 1) {
                index++;
                updateLayout();
            }
        } else if (buttonId == 18) {
            JustEnoughCalculation.proxy.getPlayerHandler().removeRecipe(recipes.get(index), null);
            JustEnoughCalculation.networkWrapper.sendToServer(new PacketRecipeUpdate(null, recipes.get(index)));
            mc.displayGuiScreen(parent);
        }
    }

    /*@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton){
        super.mouseClicked(mouseX, mouseY, mouseButton);
        Slot slot = getSlotUnderMouse(mouseX, mouseY);
        if(slot != null){
            GuiRecipeEditor recipeEditor = new GuiRecipeEditor(new ContainerRecipeEditor(), parent, recipes.get(index));
            mc.displayGuiScreen(recipeEditor);
            recipeEditor.displayRecipe(JustEnoughCalculation.proxy.getPlayerHandler().getRecipe(recipes.get(index), null));
            recipeEditor.click(mouseX, mouseY, mouseButton);
            if(slot.getStack() == null){
                recipeEditor.setActiveSlot(slot.getSlotIndex());
            }
        }
    }*/

    public void updateLayout() {
        displayRecipe(JustEnoughCalculation.proxy.getPlayerHandler().getRecipe(recipes.get(index), null));
        buttonRight.enabled = index != recipes.size() - 1;
        buttonLeft.enabled = index != 0;
    }
}
