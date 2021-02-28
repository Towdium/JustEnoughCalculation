package me.towdium.jecalculation.gui.guis.calculator;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import me.towdium.jecalculation.JecaConfig;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.core.Calculator;
import me.towdium.jecalculation.core.CostRecord;
import me.towdium.jecalculation.core.ItemStackWrapper;
import me.towdium.jecalculation.gui.commom.GuiTooltipScreen;
import me.towdium.jecalculation.gui.commom.recipe.ContainerRecipe;
import me.towdium.jecalculation.gui.guis.recipeEditor.ContainerRecipeEditor;
import me.towdium.jecalculation.gui.guis.recipeEditor.GuiRecipeEditor;
import me.towdium.jecalculation.gui.guis.recipePicker.GuiRecipePicker;
import me.towdium.jecalculation.gui.guis.recipeViewer.ContainerRecipeViewer;
import me.towdium.jecalculation.gui.guis.recipeViewer.GuiRecipeViewer;
import me.towdium.jecalculation.network.packets.PacketSyncRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.util.*;

/**
 * @author Towdium
 */
public class GuiCalculator extends GuiTooltipScreen {
    static int recentLen = 6;
    GuiTextField textFieldAmount;
    GuiButton buttonLeft;
    GuiButton buttonRight;
    GuiButton buttonEdit;
    GuiButton buttonMode;
    //GuiButton buttonCalculate;
    GuiButton buttonView;
    CostRecord costRecord;
    Map<Integer, Integer> items;
    int activeSlot = -1;
    int page = 1;
    int total = 0;
    EnumMode mode = EnumMode.INPUT;
    ItemStack buffer;
    boolean init = false;

    public enum EnumMode {
        INPUT, PROCEDURE, OUTPUT, CATALYST;

        int toInt() {
            switch (this) {
                case INPUT:
                    return 1;
                case PROCEDURE:
                    return 2;
                case OUTPUT:
                    return 3;
                case CATALYST:
                    return 4;
                default:
                    return 1;
            }
        }

        static EnumMode fromInt(int i) {
            switch (i) {
                case 1:
                    return INPUT;
                case 2:
                    return PROCEDURE;
                case 3:
                    return OUTPUT;
                case 4:
                    return CATALYST;
                default:
                    return INPUT;
            }
        }
    }

    public GuiCalculator(ContainerCalculator containerCalculator) {
        super(containerCalculator, null);
        JustEnoughCalculation.network.sendToServer(new PacketSyncRecord());
        items = new HashMap<>(27);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonLeft = new GuiButton(4, guiLeft + 7, guiTop + 139, 20, 20, "<");
        buttonRight = new GuiButton(5, guiLeft + 65, guiTop + 139, 20, 20, ">");
        buttonEdit = new GuiButton(3, guiLeft + 7, guiTop + 53, 79, 20,
                                   StatCollector.translateToLocal("gui.calculator.edit"));
        //buttonCalculate = new GuiButton(1, guiLeft+111, guiTop+7, 58, 20, StatCollector.translateToLocal("gui.calculator.calculate"));
        buttonView = new GuiButton(7, guiLeft + 90, guiTop + 53, 79, 20,
                                   StatCollector.translateToLocal("gui.calculator.view"));
        //buttonList.add(buttonCalculate);
        buttonList.add(new GuiButton(2, guiLeft + 101, guiTop + 7, 68, 20,
                                     StatCollector.translateToLocal("gui.calculator.add")));
        buttonList.add(buttonEdit);
        buttonList.add(buttonView);
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        buttonMode = new GuiButton(6, guiLeft + 89, guiTop + 139, 80, 20,
                                   StatCollector.translateToLocal("gui.calculator.input"));
        buttonList.add(buttonMode);
        textFieldAmount = new GuiTextField(fontRendererObj, guiLeft + 39, guiTop + 8, 57, 18);
        Slot dest = inventorySlots.getSlot(0);
        ItemStack itemStack = ((ContainerCalculator) inventorySlots).getPlayer().getHeldItem();
        dest.inventory.setInventorySlotContents(dest.getSlotIndex(), ItemStackWrapper.NBT.getItem(itemStack, "dest"));
        ItemStack calculatorItem = ((ContainerCalculator) inventorySlots).getPlayer().getHeldItem();
        for (int i = 0; i < recentLen; i++) {
            inventorySlots.getSlot(28 + i).putStack(ItemStackWrapper.NBT.getItem(calculatorItem, "recent" + i));
        }
        textFieldAmount.setText(ItemStackWrapper.NBT.getString(itemStack, "text"));
        mode = EnumMode.fromInt(ItemStackWrapper.NBT.getInt(calculatorItem, "mode"));
        init = true;
        onOpen();
        if (!JecaConfig.initialized) {
            ArrayList<String> idents = new ArrayList<>();
            LOOP:
            for (ICraftingHandler handler : GuiCraftingRecipe.craftinghandlers) {
                if (handler instanceof TemplateRecipeHandler) {
                    if (((TemplateRecipeHandler) handler).getOverlayIdentifier() == null) {
                        continue;
                    }
                    for (String id : idents) {
                        if (((TemplateRecipeHandler) handler).getOverlayIdentifier().equals(id)) {
                            continue LOOP;
                        }
                    }
                    idents.add(((TemplateRecipeHandler) handler).getOverlayIdentifier());
                }
            }
            String[] strings = new String[idents.size()];
            for (int i = 0; i < idents.size(); i++) {
                strings[i] = idents.get(i);
            }
            JecaConfig.EnumItems.ListRecipeCategory.getProperty().set(strings);
            JecaConfig.save();
            JecaConfig.initialized = true;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(
                new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiCalculator.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (activeSlot == 0) {
            Slot slot = inventorySlots.getSlot(activeSlot);
            this.drawTexturedModalRect(this.guiLeft + slot.xDisplayPosition - 2,
                                       this.guiTop + slot.yDisplayPosition - 2, 176, 0, 20, 20);
        } else if (activeSlot > 0) {
            Slot slot = inventorySlots.getSlot(activeSlot);
            this.drawTexturedModalRect(this.guiLeft + slot.xDisplayPosition - 1,
                                       this.guiTop + slot.yDisplayPosition - 1, 196, 0, 18, 18);
        }
        textFieldAmount.drawTextBox();
        drawMissingTexture();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
        fontRendererObj.drawString("x", 30, 13, 4210752);
        drawCenteredStringWithoutShadow(fontRendererObj, StatCollector.translateToLocal("gui.calculator.recent"), 143,
                                        36, 4210752);
        drawCenteredString(fontRendererObj, page + "/" + total, 46, 145, 0xFFFFFF);
    }

    @Override
    protected String GetButtonTooltip(int buttonId) {
        switch (buttonId) {
            case 2:
                return StatCollector.translateToLocal("gui.calculator.addTooltip");
            case 3:
                return StatCollector.translateToLocal("gui.calculator.editTooltip");
            case 6:
                return StatCollector.translateToLocal("gui.calculator.modeTooltip");
            case 7:
                return StatCollector.translateToLocal("gui.calculator.viewTooltip");
        }
        return null;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            /*case 1:
                if(inventorySlots.getSlot(0).getStack() != null){
                    mode = EnumMode.INPUT;
                    refreshRecipe();
                }
                break;*/
            case 2:
                JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(),
                                                                                  textFieldAmount.getText());
                mc.displayGuiScreen(new GuiRecipeEditor(new ContainerRecipeEditor(), this));
                break;
            case 3:
                List<Integer> list;
                if (activeSlot == -1) {
                    list = JustEnoughCalculation.proxy.getPlayerHandler()
                                                      .getAllRecipeIndexOf(inventorySlots.getSlot(0).getStack(), null);
                } else {
                    list = JustEnoughCalculation.proxy.getPlayerHandler().getAllRecipeIndexOf(
                            inventorySlots.getSlot(activeSlot).getStack(), null);
                }

                mc.displayGuiScreen(new GuiRecipePicker(new ContainerRecipe(), this, list));
                break;
            case 4:
                if (page > 1) {
                    page--;
                }
                break;
            case 5:
                if (page < total) {
                    page++;
                }
                break;
            case 6:
                switch (mode) {
                    case INPUT:
                        mode = EnumMode.PROCEDURE;
                        break;
                    case PROCEDURE:
                        mode = EnumMode.OUTPUT;
                        break;
                    case OUTPUT:
                        mode = EnumMode.CATALYST;
                        break;
                    case CATALYST:
                        mode = EnumMode.INPUT;
                        break;
                }
                ItemStack calculatorItem = ((ContainerCalculator) inventorySlots).getPlayer().getHeldItem();
                ItemStackWrapper.NBT.setInt(calculatorItem, "mode", mode.toInt());
                JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(),
                                                                                  textFieldAmount.getText());
                page = 1;
                break;
            case 7:
                mc.displayGuiScreen(new GuiRecipeViewer(new ContainerRecipeViewer(), this));
                break;
        }
        updateLayout();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        //+JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(), textFieldAmount.getText());
        textFieldAmount.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 1 && buttonMode.mousePressed(mc, mouseX, mouseY)) {
            switch (mode) {
                case INPUT:
                    mode = EnumMode.CATALYST;
                    break;
                case PROCEDURE:
                    mode = EnumMode.INPUT;
                    break;
                case OUTPUT:
                    mode = EnumMode.PROCEDURE;
                    break;
                case CATALYST:
                    mode = EnumMode.OUTPUT;
                    break;
            }
            mc.thePlayer.playSound("random.click", 0.8f, 0.8f);
            updateLayout();
            ItemStack calculatorItem = ((ContainerCalculator) inventorySlots).getPlayer().getHeldItem();
            ItemStackWrapper.NBT.setInt(calculatorItem, "mode", mode.toInt());
        }
        Slot s = getSlotUnderMouse(mouseX, mouseY);
        if (s != null && s.getSlotIndex() > 27 && s.getStack() != null) {
            inventorySlots.getSlot(0).putStack(s.getStack());
            mode = EnumMode.INPUT;
            refreshRecipe();
            updateLayout();
        } else if (s != null && s.getSlotIndex() > 0 && s.getSlotIndex() <= 27 && s.getStack() != null) {
            GuiCraftingRecipe.openRecipeGui("item", s.getStack().copy());
        }
        JustEnoughCalculation.proxy.getPlayerHandler()
                                   .syncItemCalculator(inventorySlots.getSlot(0).getStack(), textFieldAmount.getText());
        //Slot slot = getSlotUnderMouse(mouseX, mouseY);
        /*if(slot != null && slot.getSlotIndex() == 0 && mouseButton == 0){
            setActiveSlot(slot.getSlotIndex());
            ((ContainerCalculator)inventorySlots).getPlayer().playSound("random.click", 1f, 1f );
            updateLayout();
        }*/

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!this.textFieldAmount.textboxKeyTyped(typedChar, keyCode)) {
            if (keyCode == 1) {
                if (activeSlot != -1) {
                    inventorySlots.getSlot(activeSlot).putStack(buffer);
                    setActiveSlot(-1);
                } else {
                    super.keyTyped(typedChar, keyCode);
                }
            }
        } else {
            JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(),
                                                                              textFieldAmount.getText());

            refreshRecipe();
            updateLayout();
        }
    }

    public void updateLayout() {
        switch (mode) {
            case OUTPUT:
                buttonMode.displayString = StatCollector.translateToLocal("gui.calculator.output");
                break;
            case PROCEDURE:
                buttonMode.displayString = StatCollector.translateToLocal("gui.calculator.procedure");
                break;
            case INPUT:
                buttonMode.displayString = StatCollector.translateToLocal("gui.calculator.input");
                break;
            case CATALYST:
                buttonMode.displayString = StatCollector.translateToLocal("gui.calculator.catalyst");
                break;
        }
        boolean b = JustEnoughCalculation.proxy.getPlayerHandler()
                                               .getHasRecipeOf(inventorySlots.getSlot(0).getStack(), null);
        if (activeSlot == -1) {
            buttonEdit.enabled = b;
        } else {
            buttonEdit.enabled = JustEnoughCalculation.proxy.getPlayerHandler().getHasRecipeOf(
                    inventorySlots.getSlot(activeSlot).getStack(), null);
        }
        //buttonCalculate.enabled = b;
        buttonView.enabled = JustEnoughCalculation.proxy.getPlayerHandler().getHasRecipe(null);
        if (costRecord != null) {
            switch (mode) {
                case OUTPUT:
                    total = (costRecord.getOutputStack().size() + 26) / 27;
                    fillSlotsWith(costRecord.getOutputStack(), (page - 1) * 27);
                    break;
                case PROCEDURE:
                    total = (costRecord.getProcedureStack().size() + 26) / 27;
                    fillSlotsWith(costRecord.getProcedureStack(), (page - 1) * 27);
                    break;
                case INPUT:
                    total = (costRecord.getInputStack().size() + 26) / 27;
                    fillSlotsWith(costRecord.getInputStack(), (page - 1) * 27);
                    break;
                case CATALYST:
                    total = (costRecord.getCatalystStack().size() + 26) / 27;
                    fillSlotsWith(costRecord.getCatalystStack(), (page - 1) * 27);
                    break;
            }
        } else {
            fillSlotsWith(new ArrayList<ItemStack>(), 0);
            total = 0;
        }
        buttonLeft.enabled = page != 1;
        buttonRight.enabled = page < total;
        if (JecaConfig.EnumItems.EnableInventoryCheck.getProperty().getBoolean()) {
            checkItem();
        }
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    public void setActiveSlot(int activeSlot) {
        if (activeSlot == -1) {
            JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(),
                                                                              textFieldAmount.getText());
        } else {
            buffer = inventorySlots.getSlot(activeSlot).getStack();
        }
        this.activeSlot = activeSlot;
        updateLayout();
    }

    public void onOpen() {
        if (inventorySlots.getSlot(0).getStack() != null) {
            refreshRecipe();
        }
        if (init) {
            updateLayout();
        }
    }

    protected void fillSlotsWith(List<ItemStack> itemStacks, int start) {
        int pos = 1;
        for (int i = start; i < start + 27; i++) {
            if (i <= itemStacks.size() - 1) {
                ItemStack buffer = itemStacks.get(i);
                ItemStackWrapper.NBT.setBool(buffer, JustEnoughCalculation.Reference.MODID, true);
                inventorySlots.getSlot(pos++).putStack(buffer);
            } else {
                inventorySlots.getSlot(pos++).putStack(null);
            }
        }
    }

    protected void refreshRecipe() {
        ItemStack dest = inventorySlots.getSlot(0).getStack();
        int i;
        try {
            i = Integer.valueOf(textFieldAmount.getText());
        } catch (NumberFormatException e) {
            i = 1;
            textFieldAmount.setTextColor(16711680);
            TimerTask r = new TimerTask() {
                @Override
                public void run() {
                    textFieldAmount.setTextColor(14737632);
                }
            };
            Timer t = new Timer();
            t.schedule(r, 1000);
            if (inventorySlots.getSlot(0).getStack() == null) {
                return;
            }
        }
        if (dest == null) {
            return;
        }
        Calculator calculator = new Calculator(dest, i * 100);
        costRecord = calculator.getCost();
        dest = dest.copy();
        dest.getTagCompound().removeTag(JustEnoughCalculation.Reference.MODID);
        ItemStack calculatorItem = ((ContainerCalculator) inventorySlots).getPlayer().getHeldItem();
        ItemStack[] stack = new ItemStack[6];
        for (int j = 0; j < recentLen; j++) {
            stack[j] = ItemStackWrapper.NBT.getItem(calculatorItem, "recent" + j);
        }
        int duplicate = -1;
        for (int j = 0; j < recentLen; j++) {
            if (stack[j] != null && ItemStackWrapper.isTypeEqual(dest, stack[j])) {
                duplicate = j;
            }
        }
        if (duplicate == -1) {
            ItemStackWrapper.NBT.setItem(calculatorItem, "recent0", dest);
            inventorySlots.getSlot(28).putStack(dest);
            for (int j = 0; j < recentLen - 1; j++) {
                ItemStackWrapper.NBT.setItem(calculatorItem, "recent" + (j + 1), stack[j]);
                inventorySlots.getSlot(27 + 2 + j).putStack(stack[j]);
            }
        } else {
            ItemStackWrapper.NBT.setItem(calculatorItem, "recent0", dest);
            inventorySlots.getSlot(28).putStack(dest);
            for (int j = 0; j < duplicate; j++) {
                ItemStackWrapper.NBT.setItem(calculatorItem, "recent" + (j + 1), stack[j]);
                inventorySlots.getSlot(27 + 2 + j).putStack(stack[j]);
            }
        }
        JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(dest, textFieldAmount.getText());
    }

    protected void drawMissingTexture() {
        for (int i = 0; i < 27; i++) {
            Slot slot = inventorySlots.getSlot(i + 1);
            if (items.containsKey(i)) {
                int store = items.get(i);
                int a = store == 0 ? 0 : store * 0x8000000 + 0x12ff0000;
                drawRect(slot.xDisplayPosition + guiLeft - 1, slot.yDisplayPosition + guiTop - 1,
                         slot.xDisplayPosition + guiLeft + 17, slot.yDisplayPosition + guiTop + 17, a);
            }
        }
    }

    protected void checkItem() {
        if (mode != EnumMode.INPUT) {
            for (int i = 0; i < 27; i++) {
                items.put(i, 0);
            }
            return;
        }
        for (int i = 0; i < 27; i++) {
            ItemStack itemStack = inventorySlots.getSlot(i + 1).getStack();
            if (itemStack == null) {
                items.put(i, 0);
                continue;
            }
            int amount = 0;
            for (ItemStack itemStackPlayer : mc.thePlayer.inventory.mainInventory) {
                if (ItemStackWrapper.isTypeEqual(itemStackPlayer, itemStack)) {
                    amount += itemStackPlayer.stackSize;
                }
            }
            int d = (int) (amount / (double) ItemStackWrapper.getGhostItemAmount(itemStack) * 15);
            d = d > 15 ? 0 : 15 - d;
            items.put(i, d);
        }
    }

    @Override
    public boolean handleDragNDrop(GuiContainer guiContainer, int i, int i1, ItemStack itemStack, int i2) {
        Slot slot = getSlotUnderMouse(i, i1);
        if (slot != null && slot.getSlotIndex() == 0) {
            ItemStack buffer = itemStack.copy();
            buffer.stackSize = 1;
            ItemStackWrapper.NBT.setBool(buffer, JustEnoughCalculation.Reference.MODID, true);
            ItemStackWrapper.NBT.setBool(buffer, "mark", true);
            slot.putStack(buffer);
            mode = EnumMode.INPUT;
            refreshRecipe();
            updateLayout();
            JustEnoughCalculation.proxy.getPlayerHandler().syncItemCalculator(inventorySlots.getSlot(0).getStack(),
                                                                              textFieldAmount.getText());
        }
        itemStack.stackSize = 0;
        return false;
    }
}