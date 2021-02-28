package me.towdium.jecalculation.gui.guis.recipeEditor;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.core.Recipe;
import me.towdium.jecalculation.gui.commom.recipe.ContainerRecipe;
import me.towdium.jecalculation.gui.commom.recipe.GuiRecipe;
import me.towdium.jecalculation.network.packets.PacketRecipeUpdate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraft.util.StatCollector;

/**
 * @author Towdium
 */
public class GuiRecipeEditor extends GuiRecipe {
    int index = -1;

    public GuiRecipeEditor(ContainerRecipeEditor containerRecipeEditor, GuiScreen parent, int index) {
        super(containerRecipeEditor, parent);
        this.index = index;
    }

    public GuiRecipeEditor(ContainerRecipeEditor containerRecipeEditor, GuiScreen parent) {
        super(containerRecipeEditor, parent);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(16, guiLeft + 125, guiTop + 7, 44, 20,
                                     StatCollector.translateToLocal("gui.recipeEditor.save")));
        buttonList.add(new GuiButton(17, guiLeft + 125, guiTop + 31, 44, 20,
                                     StatCollector.translateToLocal("gui.recipeEditor.clear")));
        if (index != -1) {
            displayRecipe(JustEnoughCalculation.proxy.getPlayerHandler().getRecipe(index, null));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        int buttonId = button.id;
        if (buttonId == 16) {
            Recipe recipe = ((ContainerRecipe) inventorySlots).buildRecipe();
            if (recipe == null) {
                mc.displayGuiScreen(parent);
                return;
            }
            if (index == -1) {
                JustEnoughCalculation.proxy.getPlayerHandler().addRecipe(recipe, null);
                JustEnoughCalculation.networkWrapper.sendToServer(new PacketRecipeUpdate(recipe, -1));
                mc.displayGuiScreen(parent);
            } else {
                JustEnoughCalculation.proxy.getPlayerHandler().setRecipe(recipe, index, null);
                JustEnoughCalculation.networkWrapper.sendToServer(new PacketRecipeUpdate(recipe, index));
                mc.displayGuiScreen(parent);
            }
        } else if (buttonId == 17) {
            for (Object slot : inventorySlots.inventorySlots) {
                ((Slot) slot).inventory.setInventorySlotContents(((Slot) slot).getSlotIndex(), null);
            }
        }
    }
}
