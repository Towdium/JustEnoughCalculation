package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import pers.towdium.just_enough_calculation.JECConfig;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.core.Calculator;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.item.ItemLabel;
import pers.towdium.just_enough_calculation.network.packets.PacketSyncCalculator;
import pers.towdium.just_enough_calculation.util.Utilities;
import pers.towdium.just_enough_calculation.util.exception.IllegalPositionException;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.wrappers.Singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Author:  Towdium
 * Created: 2016/6/13.
 */
public class GuiCalculator extends JECGuiContainer {
    static final String keyRecipe = "recipe";
    static final String keyAmount = "amount";
    static final String keyRecent = "current";
    static final String keyMode = "mode";

    GuiTextField textFieldAmount;
    JECGuiButton buttonSearch;
    JECGuiButton buttonAdd;
    JECGuiButton buttonView;
    JECGuiButton buttonSettings;
    JECGuiButton buttonLeft;
    JECGuiButton buttonRight;
    JECGuiButton buttonMode;

    int page = 1;
    int total = 0;
    EnumMode mode = EnumMode.INPUT;

    Calculator calculatorNormal;
    Calculator calculatorInventory;

    boolean initialized = false;

    public GuiCalculator(GuiScreen parent) {
        super(new ContainerCalculator(), parent);
    }

    @Override
    public void init() {
        buttonSearch = new JECGuiButton(3, guiLeft + 119, guiTop + 7, 50, 20, "search").setLsnLeft(() ->
                Utilities.openGui(new GuiListSearch(this, inventorySlots.getSlot(27).getStack())));
        buttonAdd = new JECGuiButton(4, guiLeft + 7, guiTop + 53, 52, 20, "add").setLsnLeft(() ->
                Utilities.openGui(new GuiEditor(this, null)));
        buttonView = new JECGuiButton(5, guiLeft + 63, guiTop + 53, 52, 20, "records").setLsnLeft(() ->
                Utilities.openGui(new GuiListViewer(this)));
        buttonSettings = new JECGuiButton(6, guiLeft + 119, guiTop + 53, 50, 20, "oreDict").setLsnLeft(() ->
                Utilities.openGui(new GuiOreDict(this)));
        buttonLeft = new JECGuiButton(0, guiLeft + 7, guiTop + 139, 14, 20, "<", false).setLsnLeft(() -> {
            ++page;
            updateContent();
        });
        buttonRight = new JECGuiButton(1, guiLeft + 72, guiTop + 139, 14, 20, ">", false).setLsnLeft(() -> {
            --page;
            updateContent();
        });
        buttonMode = new JECGuiButton(2, guiLeft + 90, guiTop + 139, 79, 20, "", false).setLsnLeft(() -> {
            mode = EnumMode.values()[Utilities.circulate(mode.ordinal(), EnumMode.values().length, true)];
            updateContent();
        }).setLsnRight(() -> {
            mode = EnumMode.values()[Utilities.circulate(mode.ordinal(), EnumMode.values().length, false)];
            updateContent();
        });
        buttonList.add(buttonSearch);
        buttonList.add(buttonAdd);
        buttonList.add(buttonView);
        buttonList.add(buttonSettings);
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        buttonList.add(buttonMode);
        String temp = textFieldAmount == null ? "" : textFieldAmount.getText();
        textFieldAmount = new GuiTextField(0, fontRenderer, guiLeft + 39, guiTop + 8, 75, 18);
        textFieldAmount.setText(temp);
        textFieldAmount.setMaxStringLength(10);
        if (!initialized) {
            updateGuiFromItem();
            initialized = true;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/gui_calculator.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        textFieldAmount.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCenteredString(fontRenderer, localization("recent"), 144, 36, 0xFFFFFF);
        drawCenteredString(fontRenderer, page + "/" + total, 47, 145, 0xFFFFFF);
    }

    @Override
    protected int getSizeSlot(int index) {
        return index == 27 ? 20 : 18;
    }

    @Override
    protected BiFunction<Long, ItemStackHelper.EnumStackAmountType, String> getFormer(int id) {
        return (aLong, type) -> type.getStringResult(aLong);
    }

    @Override
    public void onItemStackSet(int index, ItemStack old) {
        ItemStack s = inventorySlots.getSlot(27).getStack();
        if (!s.isEmpty() && s.getItem() instanceof ItemLabel && ItemLabel.getName(s) == null) {
            inventorySlots.getSlot(27).putStack(old);
            Utilities.openGui(new GuiPickerLabelExisting(this, (itemStack) -> {
                inventorySlots.getSlot(27).putStack(itemStack);
                updateLayout();
                updateRecent(s);
                Utilities.openGui(this);
            }));
        } else {
            updateLayout();
            updateRecent(s);
        }
    }

    void updateRecent(ItemStack s) {
        if (s != null) {
            int indexFound = 33;
            for (int i = 33; i > 27 && indexFound == 33; i--) {
                if (inventorySlots.getSlot(i).getHasStack() && ItemStackHelper.isItemEqual(s, inventorySlots.getSlot(i).getStack()))
                    indexFound = i;
            }
            for (int i = indexFound; i > 27; i--) {
                inventorySlots.getSlot(i).putStack(inventorySlots.getSlot(i - 1).getStack());
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!textFieldAmount.textboxKeyTyped(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        } else {
            updateLayout();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textFieldAmount.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void onItemStackPick(ItemStack itemStack) {
        ItemStack s = inventorySlots.getSlot(27).getStack();
        inventorySlots.getSlot(27).putStack(itemStack);
        onItemStackSet(27, s);
    }

    public void updateContent() {
        if (calculatorNormal != null && calculatorInventory != null) {
            List<ItemStack> buffer = mode.getList(calculatorNormal, calculatorInventory);
            total = (buffer.size() + 26) / 27;
            page = total == 0 ? 0 : page > total ? total : page < 1 ? 1 : page;
            putStacks(0, 26, buffer, total == 0 ? 0 : (page - 1) * 27);
        } else {
            page = 0;
            total = 0;
            putStacks(0, 26, new ArrayList<>(), 0);
        }
        buttonMode.displayString = localization(mode.toString().toLowerCase());
    }

    @Override
    public void onGuiClosed() {
        updateItemFromGui();
        super.onGuiClosed();
    }

    @Override
    public void updateLayout() {
        long amount;
        try {
            amount = Long.parseLong(textFieldAmount.getText());
            textFieldAmount.setTextColor(0xFFFFFF);
        } catch (NumberFormatException e) {
            textFieldAmount.setTextColor(0xFF0000);
            amount = textFieldAmount.getText().equals("") ? 1 : 0;
        }
        if (inventorySlots.getSlot(27).getHasStack() && amount != 0) {
            try {
                calculatorNormal = new Calculator(inventorySlots.getSlot(27).getStack(), amount);
                calculatorInventory = new Calculator(JECConfig.EnumItems.EnableInventoryCheck.getProperty().getBoolean() ?
                        new ArrayList<>(mc.player.inventory.mainInventory) : new ArrayList<>(), inventorySlots.getSlot(27).getStack(), amount);
            } catch (Calculator.JECCalculatingCoreException e) {
                mc.player.sendMessage(new TextComponentString(localization("errorCore")));
                e.printStackTrace();
            } catch (RuntimeException e) {
                mc.player.sendMessage(new TextComponentString(localization("errorUnknown")));
                e.printStackTrace();
            }
        } else {
            calculatorNormal = null;
            calculatorInventory = null;
        }
        updateContent();
    }

    @Override
    protected int getDestSlot(int button) {
        return 27;
    }

    void updateItemFromGui() {
        Singleton<ItemStack> calc = new Singleton<>(null);
        calc.predicate = stack -> stack.getItem() == JustEnoughCalculation.itemCalculator;
        try {
            calc.push(mc.player.inventory.offHandInventory.get(0));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            calc.push(mc.player.inventory.getCurrentItem());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (calc.value != null) {
            NBTTagCompound tag = ItemStackHelper.getSubTag(calc.value, keyRecipe);
            tag.setString(keyAmount, textFieldAmount.getText());
            NBTTagList buffer = new NBTTagList();
            for (int i = 28; i <= 33; i++) {
                ItemStack s = inventorySlots.getSlot(i).getStack();
                if (s.isEmpty())
                    break;
                else
                    buffer.appendTag(s.serializeNBT());
            }
            tag.setTag(keyRecent, buffer);
            tag.setInteger(keyMode, mode.ordinal());
            JustEnoughCalculation.networkWrapper.sendToServer(new PacketSyncCalculator(calc.value));
        }
    }

    void updateGuiFromItem() {
        Singleton<ItemStack> calc = new Singleton<>(null);
        calc.predicate = stack -> stack.getItem() == JustEnoughCalculation.itemCalculator;
        try {
            calc.push(mc.player.inventory.offHandInventory.get(0));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            calc.push(mc.player.inventory.getCurrentItem());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (calc.value != null) {
            NBTTagCompound tag = ItemStackHelper.getSubTag(calc.value, keyRecipe);
            textFieldAmount.setText(tag.getString(keyAmount));
            List<ItemStack> buffer = new ArrayList<>();
            NBTTagList recent = tag.getTagList(keyRecent, 10);
            for (int i = 0; i < recent.tagCount(); i++) {
                buffer.add(new ItemStack(recent.getCompoundTagAt(i)));
            }
            putStacks(28, 33, buffer, 0);
            inventorySlots.getSlot(27).putStack(buffer.size() > 0 ? buffer.get(0) : ItemStack.EMPTY);
            mode = EnumMode.values()[tag.getInteger(keyMode)];
        }
    }

    public enum EnumMode {
        MISSING, PROCEDURE, OUTPUT, INPUT, CATALYST;

        public List<ItemStack> getList(Calculator calculatorN, Calculator calculatorI) {
            switch (this) {
                case INPUT:
                    return calculatorN.getInput();
                case MISSING:
                    return calculatorI.getInput();
                case PROCEDURE:
                    return calculatorI.getProcedure();
                case OUTPUT:
                    return calculatorI.getOutput();
                case CATALYST:
                    return calculatorI.getCatalyst();
                default:
                    throw new IllegalPositionException();
            }
        }
    }

    public static class ContainerCalculator extends JECContainer {
        @Override
        protected void addSlots() {
            addSlotGroup(8, 82, 18, 18, 3, 9);
            addSlotSingle(9, 9);
            addSlotGroup(8, 32, 18, 18, 1, 6);
        }

        @Override
        public EnumSlotType getSlotType(int index) {
            return index == 27 ? EnumSlotType.SELECT : index > 27 ? EnumSlotType.PICKER : EnumSlotType.DISABLED;
        }
    }
}
