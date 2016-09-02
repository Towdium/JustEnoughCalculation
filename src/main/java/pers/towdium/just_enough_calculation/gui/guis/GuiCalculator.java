package pers.towdium.just_enough_calculation.gui.guis;

import com.sun.istack.internal.NotNull;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.core.Calculator;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.network.packets.PacketSyncCalculator;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.Utilities;
import pers.towdium.just_enough_calculation.util.exception.IllegalPositionException;

import javax.annotation.Nullable;
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
    GuiButton buttonSearch;
    GuiButton buttonAdd;
    GuiButton buttonView;
    GuiButton buttonSettings;
    GuiButton buttonLeft;
    GuiButton buttonRight;
    GuiButton buttonMode;
    GuiButton buttonStock;

    int page = 1;
    int total = 0;
    EnumMode mode = EnumMode.INPUT;

    Calculator calculator;

    public GuiCalculator(GuiScreen parent) {
        super(new ContainerCalculator(), parent);
    }

    @Override
    public void init() {
        buttonSearch = new GuiButton(1, guiLeft + 119, guiTop + 7, 50, 20, localization("search"));
        buttonAdd = new GuiButton(2, guiLeft + 7, guiTop + 53, 52, 20, localization("add"));
        buttonView = new GuiButton(3, guiLeft + 63, guiTop + 53, 52, 20, localization("records"));
        buttonSettings = new GuiButton(4, guiLeft + 119, guiTop + 53, 50, 20, localization("oreDict"));
        buttonLeft = new GuiButton(5, guiLeft + 7, guiTop + 139, 14, 20, "<");
        buttonRight = new GuiButton(6, guiLeft + 45, guiTop + 139, 14, 20, ">");
        buttonMode = new GuiButton(7, guiLeft + 63, guiTop + 139, 52, 20, "");
        buttonStock = new GuiButton(8, guiLeft + 119, guiTop + 139, 50, 20, "");
        buttonList.add(buttonSearch);
        buttonList.add(buttonAdd);
        buttonList.add(buttonView);
        buttonList.add(buttonSettings);
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        buttonList.add(buttonMode);
        buttonList.add(buttonStock);
        String temp = textFieldAmount == null ? "" : textFieldAmount.getText();
        textFieldAmount = new GuiTextField(0, fontRendererObj, guiLeft + 39, guiTop + 8, 75, 18);
        textFieldAmount.setText(temp);
        textFieldAmount.setMaxStringLength(10);
        updateGuiFromItem();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiCalculator.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        textFieldAmount.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString("x", 30, 13, 4210752);
        drawCenteredString(fontRendererObj, localization("recent"), 144, 36, 0xFFFFFF);
        drawCenteredString(fontRendererObj, page + "/" + total, 33, 145, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                Utilities.openGui(new GuiListSearch(this, inventorySlots.getSlot(0).getStack()));
                return;
            case 2:
                Utilities.openGui(new GuiEditor(this, null));
                return;
            case 3:
                Utilities.openGui(new GuiListViewer(this));
                return;
            case 4:
                Utilities.openGui(new GuiOreDict(this));
            case 5:
                ++page;
                updateContent();
                return;
            case 6:
                --page;
                updateContent();
                return;
            case 7:
                mode = EnumMode.values()[Utilities.circulate(mode.ordinal(), 4, true)];
                updateContent();
        }
    }

    @Nullable
    @Override
    protected String getButtonTooltip(int buttonId) {
        return null;
    }

    @Override
    protected int getSizeSlot(int index) {
        return index == 0 ? 20 : 18;
    }

    @Override
    protected BiFunction<Long, ItemStackHelper.EnumStackAmountType, String> getFormer() {
        return (aLong, type) -> type.getStringResult(aLong);
    }

    @Override
    public void onItemStackSet(int index) {
        updateLayout();
        if (inventorySlots.getSlot(0).getHasStack()) {
            int indexFound = 6;
            ItemStack stack = inventorySlots.getSlot(0).getStack();
            for (int i = 6; i > 0 && indexFound == 6; i--) {
                if (inventorySlots.getSlot(i).getHasStack() && ItemStackHelper.isItemEqual(stack, inventorySlots.getSlot(i).getStack()))
                    indexFound = i;
            }
            for (int i = indexFound; i > 0; i--) {
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
        if (buttonMode.isMouseOver() && mouseButton == 1) {
            mode = EnumMode.values()[Utilities.circulate(mode.ordinal(), 4, false)];
            updateContent();
            mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void onItemStackPick(ItemStack itemStack) {
        inventorySlots.getSlot(0).putStack(itemStack);
        onItemStackSet(0);
    }

    public void updateContent() {
        if (calculator != null) {
            List<ItemStack> buffer = mode.getList(calculator);
            total = (buffer.size() + 26) / 27;
            page = total == 0 ? 0 : page > total ? total : page < 1 ? 1 : page;
            putStacks(7, 33, buffer, total == 0 ? 0 : (page - 1) * 27);
        } else {
            page = 0;
            total = 0;
            putStacks(7, 33, new ArrayList<>(), 0);
        }
        buttonMode.displayString = mode.getDisplay();
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
            amount = 0;
        }
        if (inventorySlots.getSlot(0).getHasStack() && amount != 0) {
            try {
                calculator = new Calculator(inventorySlots.getSlot(0).getStack(), amount);
            } catch (RuntimeException e) {
                mc.thePlayer.addChatMessage(new TextComponentString(localization("error")));
                e.printStackTrace();
            }
        } else {
            calculator = null;
        }
        updateContent();
    }

    void updateItemFromGui() {
        ItemStack stack = mc.thePlayer.inventory.getCurrentItem();
        if (stack != null && stack.getItem() == JustEnoughCalculation.itemCalculator) {
            NBTTagCompound tag = stack.getSubCompound(keyRecipe, true);
            tag.setString(keyAmount, textFieldAmount.getText());
            NBTTagList buffer = new NBTTagList();
            for (int i = 1; i <= 6; i++) {
                ItemStack s = inventorySlots.getSlot(i).getStack();
                if (s == null)
                    break;
                else
                    buffer.appendTag(s.serializeNBT());
            }
            tag.setTag(keyRecent, buffer);
            tag.setInteger(keyMode, mode.ordinal());
        }
        JustEnoughCalculation.networkWrapper.sendToServer(new PacketSyncCalculator(stack));
    }

    void updateGuiFromItem() {
        ItemStack stack = mc.thePlayer.inventory.getCurrentItem();
        if (stack != null && stack.getItem() == JustEnoughCalculation.itemCalculator) {
            NBTTagCompound tag = stack.getSubCompound(keyRecipe, true);
            textFieldAmount.setText(tag.getString(keyAmount));
            List<ItemStack> buffer = new ArrayList<>();
            NBTTagList recent = tag.getTagList(keyRecent, 10);
            for (int i = 0; i < recent.tagCount(); i++) {
                buffer.add(ItemStack.loadItemStackFromNBT(recent.getCompoundTagAt(i)));
            }
            putStacks(1, 6, buffer, 0);
            inventorySlots.getSlot(0).putStack(buffer.size() > 0 ? buffer.get(0) : null);
            mode = EnumMode.values()[tag.getInteger(keyMode)];
        }
        updateLayout();
    }

    public enum EnumMode {
        INPUT, PROCEDURE, OUTPUT, CATALYST;

        public String getDisplay() {
            switch (this) {
                case INPUT:
                    return JECGuiContainer.localization(GuiCalculator.class, "input");
                case PROCEDURE:
                    return JECGuiContainer.localization(GuiCalculator.class, "procedure");
                case OUTPUT:
                    return JECGuiContainer.localization(GuiCalculator.class, "output");
                case CATALYST:
                    return JECGuiContainer.localization(GuiCalculator.class, "catalyst");
                default:
                    throw new IllegalPositionException();
            }
        }

        public List<ItemStack> getList(@NotNull Calculator calculator) {
            switch (this) {
                case INPUT:
                    return calculator.getInput();
                case PROCEDURE:
                    return calculator.getProcedure();
                case OUTPUT:
                    return calculator.getOutput();
                case CATALYST:
                    return calculator.getCatalyst();
                default:
                    throw new IllegalPositionException();
            }
        }
    }

    public static class ContainerCalculator extends JECContainer {
        @Override
        protected void addSlots() {
            addSlotSingle(9, 9);
            addSlotGroup(8, 32, 18, 18, 1, 6);
            addSlotGroup(8, 82, 18, 18, 3, 9);
        }

        @Override
        public EnumSlotType getSlotType(int index) {
            return index == 0 ? EnumSlotType.SELECT : index <= 6 ? EnumSlotType.PICKER : EnumSlotType.DISABLED;
        }
    }
}
