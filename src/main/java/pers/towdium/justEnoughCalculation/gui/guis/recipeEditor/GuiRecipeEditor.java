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
        RenderHelper.disableStandardItemLighting();
        if(activeSlot != -1){
            RenderTooltip(mouseX, mouseY, new String[]{"haha"});
        }
        RenderHelper.enableStandardItemLighting();
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

    //copied code start

    protected int tooltipXOffset = 0;
    protected int tooltipYOffset = 10;

    private final static int LINE_HEIGHT = 11;

    protected void RenderTooltip(int x, int y, String[] tooltip)
    {

        int tooltipWidth = GetTooltipWidth(tooltip);
        int tooltipHeight = GetTooltipHeight(tooltip);

        int tooltipX = x + tooltipXOffset;
        int tooltipY = y + tooltipYOffset;

        if(tooltipX > width - tooltipWidth - 7)
            tooltipX = width - tooltipWidth - 7;
        if(tooltipY > height -  tooltipHeight - 8)
            tooltipY = height -  tooltipHeight - 8;

        //render the background inside box
        int innerAlpha = -0xFEFFFF0;  //very very dark purple
        drawGradientRect(tooltipX, tooltipY - 1, tooltipX + tooltipWidth + 6, tooltipY, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX, tooltipY + tooltipHeight + 6, tooltipX + tooltipWidth + 6, tooltipY + tooltipHeight + 7, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX, tooltipY, tooltipX + tooltipWidth + 6, tooltipY + tooltipHeight + 6, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX - 1, tooltipY, tooltipX, tooltipY + tooltipHeight + 6, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX + tooltipWidth + 6, tooltipY, tooltipX + tooltipWidth + 7, tooltipY + tooltipHeight + 6, innerAlpha, innerAlpha);

        //render the background outside box
        int outerAlpha1 = 0x505000FF;
        int outerAlpha2 = (outerAlpha1 & 0xFEFEFE) >> 1 | outerAlpha1 & -0x1000000;
        drawGradientRect(tooltipX, tooltipY + 1, tooltipX + 1, tooltipY + tooltipHeight + 6 - 1, outerAlpha1, outerAlpha2);
        drawGradientRect(tooltipX + tooltipWidth + 5, tooltipY + 1, tooltipX + tooltipWidth + 7, tooltipY + tooltipHeight + 6 - 1, outerAlpha1, outerAlpha2);
        drawGradientRect(tooltipX, tooltipY, tooltipX + tooltipWidth + 3, tooltipY + 1, outerAlpha1, outerAlpha1);
        drawGradientRect(tooltipX, tooltipY + tooltipHeight + 5, tooltipX + tooltipWidth + 7, tooltipY + tooltipHeight + 6, outerAlpha2, outerAlpha2);

        //render the foreground text
        int lineCount = 0;
        for (String s : tooltip)
        {
            mc.fontRendererObj.drawStringWithShadow(s, tooltipX + 2, tooltipY + 2 + lineCount * LINE_HEIGHT, 0xFFFFFF);
            lineCount++;
        }
    }

    private int GetTooltipWidth(String[] tooltipArray)
    {
        int longestWidth = 0;
        for(String s : tooltipArray)
        {
            int width = mc.fontRendererObj.getStringWidth(s);
            if(width > longestWidth)
                longestWidth = width;
        }
        return longestWidth;
    }

    private int GetTooltipHeight(String[] tooltipArray)
    {
        int tooltipHeight = mc.fontRendererObj.FONT_HEIGHT - 2;
        if (tooltipArray.length > 1)
        {
            tooltipHeight += (tooltipArray.length - 1) * LINE_HEIGHT;
        }
        return tooltipHeight;
    }
}
