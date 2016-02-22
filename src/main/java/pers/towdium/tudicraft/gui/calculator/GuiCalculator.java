package pers.towdium.tudicraft.gui.calculator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import pers.towdium.tudicraft.Tudicraft;
import pers.towdium.tudicraft.core.Calculator;
import pers.towdium.tudicraft.core.ItemStackWrapper;
import pers.towdium.tudicraft.gui.GuiTooltipScreen;
import pers.towdium.tudicraft.gui.recipeEditor.ContainerRecipeEditor;
import pers.towdium.tudicraft.gui.recipeEditor.GuiRecipeEditor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Towdium
 */
public class GuiCalculator extends GuiTooltipScreen{
    GuiTextField textFieldAmount;
    GuiButton editButton;
    GuiButton modeButton;
    Calculator.CostRecord costRecord;
    int activeSlot = -1;
    int page = 1;
    int total = 1;
    EnumMode mode = EnumMode.INPUT;

    public enum EnumMode {INPUT, OUTPUT, CATALYST}

    public GuiCalculator(ContainerCalculator containerCalculator){
        super(containerCalculator);
    }

    @Override
    public void initGui() {
        super.initGui();
        editButton = new GuiButton(3, guiLeft+90, guiTop+7, 37, 20, StatCollector.translateToLocal("gui.calculator.edit"));
        editButton.enabled = false;
        buttonList.add(new GuiButton(1, guiLeft+7, guiTop+31, 79, 20, StatCollector.translateToLocal("gui.calculator.calculate")));
        buttonList.add(new GuiButton(2, guiLeft+131, guiTop+7, 37, 20, StatCollector.translateToLocal("gui.calculator.add")));
        buttonList.add(editButton);
        buttonList.add(new GuiButton(4, guiLeft+7, guiTop+139, 20, 20, "<"));
        buttonList.add(new GuiButton(5, guiLeft+149, guiTop+139, 20, 20, ">"));
        modeButton = new GuiButton(6, guiLeft+90, guiTop+31, 79, 20, StatCollector.translateToLocal("gui.calculator.input"));
        buttonList.add(modeButton);
        textFieldAmount = new GuiTextField(0, fontRendererObj, guiLeft+39, guiTop+8, 45, 18);
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
        fontRendererObj.drawString("x", 30, 13, 4210752);
        drawCenteredStringWithoutShadow(fontRendererObj, page + " / " + total, 88, 145, 4210752);
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
                    try{
                        int i = Integer.valueOf(textFieldAmount.getText());
                        Calculator calculator = new Calculator(inventorySlots.getSlot(0).getStack(), i);
                        Calculator.CostRecord record = calculator.getCost();
                        record.unify();
                        costRecord = record;
                        updateScreen();
                    }catch (Exception e){
                        textFieldAmount.setTextColor(16711680);
                        TimerTask r = new TimerTask() {
                            @Override
                            public void run() {
                                textFieldAmount.setTextColor(14737632);
                            }
                        };
                        Timer t = new Timer();
                        t.schedule(r, 1000);
                    }
                }
                break;
            case 2:
                Tudicraft.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(), textFieldAmount.getText());
                mc.displayGuiScreen(new GuiRecipeEditor(new ContainerRecipeEditor(((ContainerCalculator)inventorySlots).getPlayer()), this));
                break;
            case 6:
                switch (mode){
                    case INPUT:
                        mode = EnumMode.OUTPUT;
                        break;
                    case OUTPUT:
                        mode = EnumMode.CATALYST;
                        break;
                    case CATALYST:
                        mode = EnumMode.INPUT;
                        break;
                }
                updateScreen();
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
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.textFieldAmount.textboxKeyTyped(typedChar, keyCode)){

            if(keyCode == 1){
                Tudicraft.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(), textFieldAmount.getText());
                ((ContainerCalculator)inventorySlots).getPlayer().closeScreen();
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

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        if (slotIn != null)
        {
            slotId = slotIn.slotNumber;
        }
        mc.thePlayer.openContainer.slotClick(slotId, clickedButton, clickType, mc.thePlayer);
    }

    public void updateScreen(){
        switch (mode){
            case OUTPUT:
                modeButton.displayString = StatCollector.translateToLocal("gui.calculator.output");
                break;
            case INPUT:
                modeButton.displayString = StatCollector.translateToLocal("gui.calculator.input");
                break;
            case CATALYST:
                modeButton.displayString = StatCollector.translateToLocal("gui.calculator.catalyst");
                break;
        }
        editButton.enabled = Tudicraft.proxy.getPlayerHandler().getHasRecipeOf(inventorySlots.getSlot(0).getStack());



        if(costRecord != null){
            switch (mode){
                case OUTPUT:
                    fillSlotsWith(costRecord.getOutputStack(), (page-1)*36);break;
                case INPUT:
                    fillSlotsWith(costRecord.getInputStack(), (page-1)*36);break;
                case CATALYST:
                    fillSlotsWith(costRecord.getCatalystStack(), (page-1)*36);break;
            }
        }
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    public void setActiveSlot(int activeSlot) {
        this.activeSlot = activeSlot;
        if(activeSlot == -1){
            Tudicraft.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(), textFieldAmount.getText());
        }
    }

    public void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
    }

    private void fillSlotsWith(ItemStack[] itemStacks, int start){
        int pos = 1;
        for(int i=start ;i<=itemStacks.length-1 && i<=start+36; i++){
            inventorySlots.getSlot(pos++).putStack(itemStacks[i]);
        }
    }
}
