package pers.towdium.justEnoughCalculation.gui.guis.recipeEditor;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.util.StatCollector;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.core.Recipe;
import pers.towdium.justEnoughCalculation.gui.commom.recipe.ContainerRecipe;
import pers.towdium.justEnoughCalculation.gui.commom.recipe.GuiRecipe;
import pers.towdium.justEnoughCalculation.network.packets.PacketRecipeUpdate;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Towdium
 */
public class GuiRecipeEditor extends GuiRecipe{
    int index = -1;

    public GuiRecipeEditor(ContainerRecipeEditor containerRecipeEditor, GuiScreen parent, int index){
        super(containerRecipeEditor, parent);
        this.index = index;
    }

    public GuiRecipeEditor(ContainerRecipeEditor containerRecipeEditor, GuiScreen parent){
        super(containerRecipeEditor, parent);
    }

    public GuiRecipeEditor(ContainerRecipeEditor containerRecipeEditor){
        super(containerRecipeEditor);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(16, guiLeft+125, guiTop+7, 44, 20, StatCollector.translateToLocal("gui.recipeEditor.save")));
        buttonList.add(new GuiButton(17, guiLeft+125, guiTop+31, 44, 20, StatCollector.translateToLocal("gui.recipeEditor.clear")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        int buttonId = button.id;
        if(buttonId == 16) {
            Recipe recipe = ((ContainerRecipe)inventorySlots).buildRecipe();
            if(recipe == null){
                mc.displayGuiScreen(parent);
                return;
            }
            if(index == -1){
                JustEnoughCalculation.proxy.getPlayerHandler().addRecipe(recipe, null);
                JustEnoughCalculation.networkWrapper.sendToServer(new PacketRecipeUpdate(recipe, -1));
                mc.displayGuiScreen(parent);
            }else {
                JustEnoughCalculation.proxy.getPlayerHandler().setRecipe(recipe, index, null);
                JustEnoughCalculation.networkWrapper.sendToServer(new PacketRecipeUpdate(recipe, index));
                mc.displayGuiScreen(parent);
            }
        }else if(buttonId == 17) {
            for(Slot slot : inventorySlots.inventorySlots){
                slot.inventory.setInventorySlotContents(slot.getSlotIndex(), null);
            }
        }
    }
}
