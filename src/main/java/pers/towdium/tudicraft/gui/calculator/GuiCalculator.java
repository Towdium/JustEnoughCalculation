package pers.towdium.tudicraft.gui.calculator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import pers.towdium.tudicraft.Tudicraft;
import pers.towdium.tudicraft.core.Calculator;
import pers.towdium.tudicraft.core.ItemStackWrapper;
import pers.towdium.tudicraft.gui.GuiTooltipScreen;
import pers.towdium.tudicraft.gui.recipeEditor.ContainerRecipeEditor;
import pers.towdium.tudicraft.gui.recipeEditor.GuiRecipeEditor;
import pers.towdium.tudicraft.network.packages.PackageCalculatorUpdate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Towdium
 */
public class GuiCalculator extends GuiTooltipScreen{
    GuiTextField textFieldAmount;
    GuiButton edit;
    Calculator.CostRecord costRecord;
    int activeSlot = -1;
    int page = 1;
    EnumMode mode = EnumMode.input;


    public enum EnumMode {input, output, catalyst}

    public GuiCalculator(ContainerCalculator containerCalculator){
        super(containerCalculator);
    }

    @Override
    public void initGui() {
        super.initGui();
        edit = new GuiButton(3, guiLeft+125, guiTop+31, 44, 20, StatCollector.translateToLocal("gui.calculator.edit"));
        edit.enabled = false;
        buttonList.add(new GuiButton(1, guiLeft+7, guiTop+31, 79, 20, StatCollector.translateToLocal("gui.calculator.calculate")));
        buttonList.add(new GuiButton(2, guiLeft+125, guiTop+7, 44, 20, StatCollector.translateToLocal("gui.calculator.add")));
        buttonList.add(edit);
        buttonList.add(new GuiButton(4, guiLeft+7, guiTop+139, 20, 20, "<"));
        buttonList.add(new GuiButton(5, guiLeft+149, guiTop+139, 20, 20, ">"));
        textFieldAmount = new GuiTextField(0, fontRendererObj, guiLeft+39, guiTop+8, 44, 18);
        Slot dest = inventorySlots.getSlot(0);
        ItemStack itemStack = ((ContainerCalculator)inventorySlots).getPlayer().getHeldItem();
        dest.inventory.setInventorySlotContents(dest.getSlotIndex(), ItemStackWrapper.NBT.getItem(itemStack, "dest"));
        textFieldAmount.setText(ItemStackWrapper.NBT.getString(itemStack, "text"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(Tudicraft.Reference.MODID,"textures/gui/guiCalculator.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if(activeSlot == 0){
            Slot slot = inventorySlots.getSlot(activeSlot);
            this.drawTexturedModalRect(this.guiLeft+slot.xDisplayPosition-2, this.guiTop+slot.yDisplayPosition-2, 176, 0, 20, 20);
        }else if(activeSlot > 0){
            Slot slot = inventorySlots.getSlot(activeSlot);
            this.drawTexturedModalRect(this.guiLeft+slot.xDisplayPosition-1, this.guiTop+slot.yDisplayPosition-1, 196, 0, 18, 18);
        }
        textFieldAmount.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        this.fontRendererObj.drawString("x", 30, 13, 4210752);
    }

    @Override
    protected String GetButtonTooltip(int buttonId) {
        switch (buttonId){
            case 2: return StatCollector.translateToLocal("gui.calculator.addTooltip");
            case 3: return StatCollector.translateToLocal("gui.calculator.editTooltip");
        }

        return null;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id){
            case 1:
                if(inventorySlots.getSlot(0).getStack() != null){
                    Calculator calculator = new Calculator(inventorySlots.getSlot(0).getStack(), Integer.valueOf(textFieldAmount.getText()));
                    Calculator.CostRecord record = calculator.getCost();
                    record.unify();
                    costRecord = record;
                }
                break;
            case 2:
                mc.displayGuiScreen(new GuiRecipeEditor(new ContainerRecipeEditor(((ContainerCalculator)inventorySlots).getPlayer()), this));
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textFieldAmount.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
        Slot slot = getSlotUnderMouse();
        if(slot != null && mouseButton == 0){
            activeSlot = slot.getSlotIndex();
            ((ContainerCalculator)inventorySlots).getPlayer().playSound("random.click", 1f, 1f );
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);
        //Method
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    public void setActiveSlot(int activeSlot) {
        this.activeSlot = activeSlot;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.textFieldAmount.textboxKeyTyped(typedChar, keyCode)){
            if(keyCode == 1){
                ItemStack itemStack = ((ContainerCalculator)inventorySlots).getPlayer().getHeldItem();
                ItemStackWrapper.NBT.setItem(itemStack, "dest", inventorySlots.getSlot(0).inventory.getStackInSlot(inventorySlots.getSlot(0).getSlotIndex()));
                ItemStackWrapper.NBT.setString(itemStack, "text", textFieldAmount.getText());
                Tudicraft.networkWrapper.sendToServer(new PackageCalculatorUpdate(((ContainerCalculator)inventorySlots).getPlayer().getHeldItem()));
                ((ContainerCalculator)inventorySlots).getPlayer().closeScreen();
            }
        }
    }

    public void updateScreen(){

    }

    public void updateSlots(){

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
                    super.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, "≈1.23k");
                    fr.setUnicodeFlag(b);
                }
            };
        }
    }
}
