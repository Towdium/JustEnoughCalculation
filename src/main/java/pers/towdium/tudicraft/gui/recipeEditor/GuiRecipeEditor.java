package pers.towdium.tudicraft.gui.recipeEditor;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import pers.towdium.tudicraft.Tudicraft;
import pers.towdium.tudicraft.core.Recipe;
import pers.towdium.tudicraft.network.packages.PackageRecipeUpdate;

import java.io.IOException;
import java.sql.Time;

/**
 * @author Towdium
 */
public class GuiRecipeEditor extends GuiContainer{
    GuiScreen parent;
    protected int activeSlot = -1;

    public GuiRecipeEditor(ContainerRecipeEditor containerRecipeEditor, GuiScreen parent){
        super(containerRecipeEditor);
        this.parent = parent;
    }

    public GuiRecipeEditor(ContainerRecipeEditor containerRecipeEditor){
        super(containerRecipeEditor);
    }

    @Override
    public void initGui() {
        super.initGui();
        int i, left, top;
        i = 0; left = 31 + guiLeft; top = 7 + guiTop;
        for(int a = 0; a < 2; a++){
            for(int b =0; b < 2; b++){
                buttonList.add(new GuiButton(i+2*a+b, left+b*59, top+a*24, 20, 20, "X"));
            }
        }
        i = 4; left = 31 + guiLeft; top = 67 + guiTop;
        for(int a = 0; a < 4; a++){
            for(int b =0; b < 3; b++){
                buttonList.add(new GuiButton(i+3*a+b, left+b*59, top+a*24, 20, 20, "X"));
            }
        }
        buttonList.add(new GuiButton(16, guiLeft+125, guiTop+7, 44, 20, StatCollector.translateToLocal("gui.recipeEditor.save")));
        buttonList.add(new GuiButton(17, guiLeft+125, guiTop+31, 44, 20, StatCollector.translateToLocal("gui.recipeEditor.clear")));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(Tudicraft.Reference.MODID,"textures/gui/guiRecipeEditor.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if(activeSlot != -1){
            Slot slot = inventorySlots.getSlot(activeSlot);
            this.drawTexturedModalRect(this.guiLeft+slot.xDisplayPosition-2, this.guiTop+slot.yDisplayPosition-2, 176, 0, 20, 20);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int buttonId = button.id;
        if(buttonId <= 15){
            Slot slot = inventorySlots.getSlot(buttonId);
            slot.inventory.setInventorySlotContents(slot.getSlotIndex(), null);
        }else if(buttonId == 16) {
            Recipe recipe = ((ContainerRecipeEditor)inventorySlots).buildRecipe();
            Tudicraft.proxy.getPlayerHandler().addRecipe(recipe, null);
            Tudicraft.networkWrapper.sendToServer(new PackageRecipeUpdate(recipe, -1));
            mc.displayGuiScreen(parent);
        }else if(buttonId == 17) {
            for(Slot slot : inventorySlots.inventorySlots){
                slot.inventory.setInventorySlotContents(slot.getSlotIndex(), null);
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {}

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        Slot slot = getSlotUnderMouse();
        if(slot != null && mouseButton == 0 && slot.getStack() == null){
            activeSlot = slot.getSlotIndex();
            ((ContainerRecipeEditor)inventorySlots).getPlayer().playSound("random.click", 1f, 1f );
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        if (slotIn != null)
        {
            slotId = slotIn.slotNumber;
        }
        mc.thePlayer.openContainer.slotClick(slotId, clickedButton, clickType, mc.thePlayer);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(parent);
        }
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    public void setActiveSlot(int activeSlot) {
        this.activeSlot = activeSlot;
    }
}
