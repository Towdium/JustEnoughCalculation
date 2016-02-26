package pers.towdium.justEnoughCalculation.gui.commom.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.core.ItemStackWrapper;
import pers.towdium.justEnoughCalculation.core.Recipe;
import pers.towdium.justEnoughCalculation.gui.commom.GuiTooltipScreen;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Towdium
 */
public class GuiRecipe extends GuiTooltipScreen{
    protected GuiScreen parent;
    protected int activeSlot = -1;

    public GuiRecipe(ContainerRecipe containerRecipe, GuiScreen parent){
        super(containerRecipe);
        this.parent = parent;
    }

    public GuiRecipe(ContainerRecipe containerRecipe){
        super(containerRecipe);
    }

    @Override
    public void initGui() {
        super.initGui();
        int i, left, top;
        i = 0; left = 31 + guiLeft; top = 7 + guiTop;
        for(int a = 0; a < 2; a++){
            for(int b =0; b < 2; b++){
                buttonList.add(new GuiButton(i+2*a+b, left+b*59, top+a*24, 20, 20, "N"));
            }
        }
        i = 4; left = 31 + guiLeft; top = 67 + guiTop;
        for(int a = 0; a < 4; a++){
            for(int b =0; b < 3; b++){
                buttonList.add(new GuiButton(i+3*a+b, left+b*59, top+a*24, 20, 20, "N"));
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID,"textures/gui/guiRecipe.png"));
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
            if(button.displayString.equals("N")){
                slot.putStack(ItemStackWrapper.toPercentage(slot.getStack()));
                button.displayString = "P";
            }else {
                slot.putStack(ItemStackWrapper.toNormal(slot.getStack()));
                button.displayString = "N";
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
            setActiveSlot(slot.getSlotIndex());
            mc.thePlayer.playSound("random.click", 1f, 1f );
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
            if(activeSlot != -1){
                setActiveSlot(-1);
            }else {
                mc.displayGuiScreen(parent);
            }
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        ModelManager modelManager = null;
        Field[] fields = mc.getClass().getDeclaredFields();
        for(Field field : fields){
            if(ModelManager.class.equals(field.getType())){
                field.setAccessible(true);
                try {
                    modelManager = (ModelManager) field.get(mc);
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if(modelManager != null){
            itemRender = new RenderItem(mc.renderEngine, modelManager){
                @Override
                public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
                    boolean b = fr.getUnicodeFlag();
                    fr.setUnicodeFlag(true);
                    super.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, ItemStackWrapper.getDisplayAmount(stack));
                    fr.setUnicodeFlag(b);
                }
            };
        }
    }

    public void click(int mouseX, int mouseY, int mouseButton){
        try {
            this.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException ignored) {}
    }

    public void click(int index){
        for(GuiButton button : buttonList){
            if(button.id == index){
                try {
                    actionPerformed(button);
                } catch (IOException ignored) {}
            }
        }
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    public void setActiveSlot(int activeSlot) {
        this.activeSlot = activeSlot;
    }

    public void displayRecipe(Recipe recipe){
        List<ItemStack> output = recipe.getOutput();
        List<ItemStack> input = recipe.getInput();
        for(int i=0; i<4; i++){
            if(i<output.size()){
                this.inventorySlots.getSlot(i).putStack(output.get(i));
            }else {
                this.inventorySlots.getSlot(i).putStack(null);
            }
        }
        for(int i=0; i<12; i++){
            if(i<input.size()){
                this.inventorySlots.getSlot(i+4).putStack(input.get(i));
            }else {
                this.inventorySlots.getSlot(i+4).putStack(null);
            }
        }
    }

    @Override
    protected String GetButtonTooltip(int buttonId) {
        if(buttonId == 0){
            return StatCollector.translateToLocal("gui.calculator.tooltipUse");
        }else {
            return null;
        }
    }
}
