package pers.towdium.justEnoughCalculation.gui.guis.calculator;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.core.Calculator;
import pers.towdium.justEnoughCalculation.core.CostRecord;
import pers.towdium.justEnoughCalculation.core.ItemStackWrapper;
import pers.towdium.justEnoughCalculation.gui.commom.GuiTooltipScreen;
import pers.towdium.justEnoughCalculation.gui.commom.recipe.ContainerRecipe;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.ContainerRecipeEditor;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.GuiRecipeEditor;
import pers.towdium.justEnoughCalculation.gui.guis.recipePicker.GuiRecipePicker;
import pers.towdium.justEnoughCalculation.gui.guis.recipeViewer.ContainerRecipeViewer;
import pers.towdium.justEnoughCalculation.gui.guis.recipeViewer.GuiRecipeViewer;
import pers.towdium.justEnoughCalculation.network.packets.PacketSyncRecord;

import java.util.*;

/**
 * @author Towdium
 */
public class GuiCalculator extends GuiTooltipScreen{
    GuiTextField textFieldAmount;
    GuiButton buttonLeft;
    GuiButton buttonRight;
    GuiButton buttonEdit;
    GuiButton buttonMode;
    GuiButton buttonCalculate;
    GuiButton buttonView;
    CostRecord costRecord;
    Map<Integer, Integer> items;
    int activeSlot = -1;
    int page = 1;
    int total = 0;
    EnumMode mode = EnumMode.INPUT;
    ItemStack buffer;

    public enum EnumMode {INPUT, OUTPUT, CATALYST}

    public GuiCalculator(ContainerCalculator containerCalculator){
        super(containerCalculator, null);
        JustEnoughCalculation.networkWrapper.sendToServer(new PacketSyncRecord());
        items = new HashMap<>(36);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonLeft = new GuiButton(4, guiLeft+7, guiTop+139, 20, 20, "<");
        buttonRight = new GuiButton(5, guiLeft+65, guiTop+139, 20, 20, ">");
        buttonEdit = new GuiButton(3, guiLeft+89, guiTop+7, 38, 20, StatCollector.translateToLocal("gui.calculator.edit"));
        buttonCalculate = new GuiButton(1, guiLeft+7, guiTop+31, 78, 20, StatCollector.translateToLocal("gui.calculator.calculate"));
        buttonView = new GuiButton(7, guiLeft+131, guiTop+7, 38, 20, StatCollector.translateToLocal("gui.calculator.view"));
        buttonList.add(buttonCalculate);
        buttonList.add(new GuiButton(2, guiLeft+89, guiTop+31, 80, 20, StatCollector.translateToLocal("gui.calculator.add")));
        buttonList.add(buttonEdit);
        buttonList.add(buttonView);
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        buttonMode = new GuiButton(6, guiLeft+89, guiTop+139, 80, 20, StatCollector.translateToLocal("gui.calculator.input"));
        buttonList.add(buttonMode);
        textFieldAmount = new GuiTextField(fontRendererObj, guiLeft+39, guiTop+8, 45, 18);
        Slot dest = inventorySlots.getSlot(0);
        ItemStack itemStack = ((ContainerCalculator)inventorySlots).getPlayer().getHeldItem();
        dest.inventory.setInventorySlotContents(dest.getSlotIndex(), ItemStackWrapper.NBT.getItem(itemStack, "dest"));
        textFieldAmount.setText(ItemStackWrapper.NBT.getString(itemStack, "text"));
        updateLayout();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID,"textures/gui/guiCalculator.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if(activeSlot == 0){
            Slot slot = inventorySlots.getSlot(activeSlot);
            this.drawTexturedModalRect(this.guiLeft+slot.xDisplayPosition-2, this.guiTop+slot.yDisplayPosition-2, 176, 0, 20, 20);
        }else if(activeSlot > 0){
            Slot slot = inventorySlots.getSlot(activeSlot);
            this.drawTexturedModalRect(this.guiLeft+slot.xDisplayPosition-1, this.guiTop+slot.yDisplayPosition-1, 196, 0, 18, 18);
        }
        textFieldAmount.drawTextBox();
        drawMissingTexture();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        fontRendererObj.drawString("x", 30, 13, 4210752);
        drawCenteredString(fontRendererObj, page + "/" + total, 46, 145, 0xFFFFFF);
    }

    @Override
    protected String GetButtonTooltip(int buttonId) {
        switch (buttonId){
            case 2: return StatCollector.translateToLocal("gui.calculator.addTooltip");
            case 3: return StatCollector.translateToLocal("gui.calculator.editTooltip");
            case 7: return StatCollector.translateToLocal("gui.calculator.viewTooltip");
        }
        return null;
    }

    @Override
    protected void actionPerformed(GuiButton button){
        switch (button.id){
            case 1:
                if(inventorySlots.getSlot(0).getStack() != null){
                    refreshRecipe();
                }
                break;
            case 2:
                JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(), textFieldAmount.getText());
                mc.displayGuiScreen(new GuiRecipeEditor(new ContainerRecipeEditor(), this));
                break;
            case 3:
                List<Integer> list;
                if (activeSlot == -1) {
                    list = JustEnoughCalculation.proxy.getPlayerHandler().getAllRecipeIndexOf(inventorySlots.getSlot(0).getStack(), null);
                }else {
                    list = JustEnoughCalculation.proxy.getPlayerHandler().getAllRecipeIndexOf(inventorySlots.getSlot(activeSlot).getStack(), null);
                }

                mc.displayGuiScreen(new GuiRecipePicker(new ContainerRecipe(), this, list));
                break;
            case 4:
                if(page>1){
                    page--;
                }
                break;
            case 5:
                if(page<total){
                    page++;
                }
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
                page = 1;
                break;
            case 7:
                mc.displayGuiScreen(new GuiRecipeViewer(new ContainerRecipeViewer(), this));
                break;
        }
        updateLayout();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton){
        JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(), textFieldAmount.getText());
        textFieldAmount.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
        //Slot slot = getSlotUnderMouse(mouseX, mouseY);
        /*if(slot != null && slot.getSlotIndex() == 0 && mouseButton == 0){
            setActiveSlot(slot.getSlotIndex());
            ((ContainerCalculator)inventorySlots).getPlayer().playSound("random.click", 1f, 1f );
            updateLayout();
        }*/

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode){
        if (!this.textFieldAmount.textboxKeyTyped(typedChar, keyCode)){
            if(keyCode == 1){
                if(activeSlot != -1){
                    inventorySlots.getSlot(activeSlot).putStack(buffer);
                    setActiveSlot(-1);
                }else {
                    super.keyTyped(typedChar, keyCode);
                }
            }
        }else {
            JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(), textFieldAmount.getText());
        }
    }

    public void updateLayout(){
        switch (mode){
            case OUTPUT:
                buttonMode.displayString = StatCollector.translateToLocal("gui.calculator.output");
                break;
            case INPUT:
                buttonMode.displayString = StatCollector.translateToLocal("gui.calculator.input");
                break;
            case CATALYST:
                buttonMode.displayString = StatCollector.translateToLocal("gui.calculator.catalyst");
                break;
        }
        boolean b = JustEnoughCalculation.proxy.getPlayerHandler().getHasRecipeOf(inventorySlots.getSlot(0).getStack(), null);
        if(activeSlot == -1){
            buttonEdit.enabled = b;
        }else {
            buttonEdit.enabled = JustEnoughCalculation.proxy.getPlayerHandler().getHasRecipeOf(inventorySlots.getSlot(activeSlot).getStack(), null);
        }
        buttonCalculate.enabled = b;
        buttonView.enabled = JustEnoughCalculation.proxy.getPlayerHandler().getHasRecipe(null);
        if(costRecord != null){
            switch (mode){
                case OUTPUT:
                    total = (costRecord.getOutputStack().size()+35)/36;
                    fillSlotsWith(costRecord.getOutputStack(), (page-1)*36);break;
                case INPUT:
                    total = (costRecord.getInputStack().size()+35)/36;
                    fillSlotsWith(costRecord.getInputStack(), (page-1)*36);break;
                case CATALYST:
                    total = (costRecord.getCatalystStack().size()+35)/36;
                    fillSlotsWith(costRecord.getCatalystStack(), (page-1)*36);break;
            }
        }else {
            fillSlotsWith(new ArrayList<ItemStack>(), 0);
            total = 0;
        }
        buttonLeft.enabled = page != 1;
        buttonRight.enabled = page < total;
        if(JustEnoughCalculation.JECConfig.EnumItems.EnableInventoryCheck.getProperty().getBoolean()){
            checkItem();
        }
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    public void setActiveSlot(int activeSlot) {
        if(activeSlot == -1){
            JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(), textFieldAmount.getText());
        }else {
            buffer = inventorySlots.getSlot(activeSlot).getStack();
        }
        this.activeSlot = activeSlot;
        updateLayout();
    }

    public void onOpen(){
        costRecord = null;
        if(buttonRight != null){
            updateLayout();
        }
    }

    protected void fillSlotsWith(List<ItemStack> itemStacks, int start){
        int pos = 1;
        for(int i=start; i<start+36; i++){
            if(i<=itemStacks.size()-1){
                ItemStack buffer = itemStacks.get(i);
                ItemStackWrapper.NBT.setBool(buffer, JustEnoughCalculation.Reference.MODID, true);
                inventorySlots.getSlot(pos++).putStack(buffer);
            }else {
                inventorySlots.putStackInSlot(pos++, null);
            }
        }
    }

    protected void refreshRecipe(){
        try {
            int i = Integer.valueOf(textFieldAmount.getText());
            Calculator calculator = new Calculator(inventorySlots.getSlot(0).getStack(), i*100);
            costRecord = calculator.getCost();
        } catch (NumberFormatException e){
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

    protected void drawMissingTexture(){
        for(int i=0; i<36; i++){
            Slot slot = inventorySlots.getSlot(i+1);
            if(items.containsKey(i)){
                int store = items.get(i);
                int a = store == 0 ? 0 : store*0x8000000+0x12ff0000;
                drawRect(slot.xDisplayPosition+guiLeft-1, slot.yDisplayPosition+guiTop-1, slot.xDisplayPosition+guiLeft+17, slot.yDisplayPosition+guiTop+17, a);
            }
        }
    }

    protected void checkItem(){
        if(mode != EnumMode.INPUT){
            for(int i=0; i<36; i++){
                items.put(i, 0);
            }
            return;
        }
        for(int i=0; i<36; i++){
            ItemStack itemStack = inventorySlots.getSlot(i+1).getStack();
            if(itemStack == null){
                items.put(i, 0);
                continue;
            }
            int amount = 0;
            for(ItemStack itemStackPlayer : mc.thePlayer.inventory.mainInventory){
                if(ItemStackWrapper.isTypeEqual(itemStackPlayer, itemStack)){
                    amount += itemStackPlayer.stackSize;
                }
            }
            int d = (int)(amount/(double)ItemStackWrapper.getGhostItemAmount(itemStack)*15);
            d = d>15 ? 0 : 15-d;
            items.put(i, d);
        }
    }

    @Override
    public boolean handleDragNDrop(GuiContainer guiContainer, int i, int i1, ItemStack itemStack, int i2) {
        Slot slot = getSlotUnderMouse(i, i1);
        if(slot != null && slot.getSlotIndex() == 0){
            ItemStack buffer = itemStack.copy();
            buffer.stackSize = 1;
            ItemStackWrapper.NBT.setBool(buffer, JustEnoughCalculation.Reference.MODID, true);
            ItemStackWrapper.NBT.setBool(buffer, "mark", true);
            slot.putStack(buffer);
            updateLayout();
        }
        itemStack.stackSize=0;
        return false;
    }
}