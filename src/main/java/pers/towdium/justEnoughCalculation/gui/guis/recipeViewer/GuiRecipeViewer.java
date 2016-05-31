package pers.towdium.justEnoughCalculation.gui.guis.recipeViewer;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.core.Recipe;
import pers.towdium.justEnoughCalculation.gui.commom.GuiJustEnoughCalculation;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.ContainerRecipeEditor;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.GuiRecipeEditor;
import pers.towdium.justEnoughCalculation.network.packets.PacketRecipeUpdate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

/**
 * @author Towdium
 */
public class GuiRecipeViewer extends GuiJustEnoughCalculation {
    List<Integer> recipes;
    GuiButton buttonLeft;
    GuiButton buttonRight;
    int page = 1;
    int total;

    public GuiRecipeViewer (@Nonnull ContainerRecipeViewer container, @Nullable GuiScreen parent){
        super(container, parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonLeft = new GuiButton(0, guiLeft+7, guiTop+139, 20, 20, "<");
        buttonRight = new GuiButton(1, guiLeft+149, guiTop+139, 20, 20, ">");
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        for(int i=0; i<6; i++){
            buttonList.add(new GuiButtonExt(2+2*i, guiLeft+83, guiTop+7+22*i, 41, 18, StatCollector.translateToLocal("gui.recipeViewer.edit")));
            buttonList.add(new GuiButtonExt(3+2*i, guiLeft+128, guiTop+7+22*i, 41, 18, StatCollector.translateToLocal("gui.recipeViewer.delete")));
        }
        updateLayout();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID,"textures/gui/guiRecipeViewer.png"));
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawCenteredString(fontRendererObj, page + "/" + total, 88, 145, 0xFFFFFF);
    }

    @Override
    public void updateLayout() {
        recipes = JustEnoughCalculation.proxy.getPlayerHandler().getAllRecipeIndex(null);
        total = recipes == null ? 0 : (recipes.size()+5)/6;
        if(page>total && page != 1){
            page = total;
        }
        displayRecipes();
        for(int i=0; i<6; i++){
            boolean flag = recipes.size() > (page-1)*6+i;
            buttonList.get(2+2*i).enabled = flag;
            buttonList.get(3+2*i).enabled = flag;
        }
        buttonLeft.enabled = page != 1;
        buttonRight.enabled = page < total;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int i = button.id;
        switch (i){
            case 0:
                page--;
                updateLayout();
                break;
            case 1:
                page++;
                updateLayout();
                break;
            default:
                if(i%2 == 0){
                    mc.displayGuiScreen(new GuiRecipeEditor(new ContainerRecipeEditor(), this, recipes.get((page-1)*6+i/2-1)));
                }else{
                    JustEnoughCalculation.proxy.getPlayerHandler().removeRecipe(recipes.get((page-1)*6+i/2-1), null);
                    JustEnoughCalculation.networkWrapper.sendToServer(new PacketRecipeUpdate(null, recipes.get((page-1)*6+i/2-1)));
                    updateLayout();
                }
        }
    }

    /**
     * display the recipe at the position
     * @param position range 1-6
     */
    public void displayRecipe(@Nullable Recipe recipe, int position){
        if(recipe != null){
            List<ItemStack> items = recipe.getOutput();
            for(int i=0; i<4; i++){
                if(i<items.size()){
                    inventorySlots.getSlot((position-1)*4+i).putStack(items.get(i));
                }else {
                    inventorySlots.getSlot((position-1)*4+i).putStack(null);
                }
            }
        }else {
            for(int i=0; i<4; i++){
                inventorySlots.getSlot((position-1)*4+i).putStack(null);
            }
        }
    }

    public void displayRecipes(){
        if(recipes != null){
            for(int i=1; i<=6; i++){
                int index = i-1+6*(page-1);
                if(index<recipes.size()){
                    displayRecipe(JustEnoughCalculation.proxy.getPlayerHandler().getRecipe(recipes.get(index), null), i);
                }else {
                    displayRecipe(null, i);
                }
            }
        }else {
            for(int i=1; i<=6; i++){
                displayRecipe(null, i);
            }
        }
    }
}
