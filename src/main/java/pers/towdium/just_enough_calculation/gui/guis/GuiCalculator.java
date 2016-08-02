package pers.towdium.just_enough_calculation.gui.guis;

import com.sun.istack.internal.NotNull;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.core.Calculator;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
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
    public void initGui() {
        super.initGui();
        buttonSearch = new GuiButton(1, guiLeft + 119, guiTop + 7, 50, 20, "search");
        buttonAdd = new GuiButton(2, guiLeft + 7, guiTop + 53, 52, 20, "Add");
        buttonView = new GuiButton(3, guiLeft + 63, guiTop + 53, 52, 20, "Records");
        buttonSettings = new GuiButton(4, guiLeft + 119, guiTop + 53, 50, 20, "Settings");
        buttonLeft = new GuiButton(5, guiLeft + 7, guiTop + 139, 14, 20, "<");
        buttonRight = new GuiButton(6, guiLeft + 45, guiTop + 139, 14, 20, ">");
        buttonMode = new GuiButton(7, guiLeft + 63, guiTop + 139, 52, 20, "");
        buttonStock = new GuiButton(8, guiLeft + 119, guiTop + 139, 50, 20, "Stock");
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
        updateLayout();
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
        drawCenteredStringWithoutShadow(fontRendererObj, "Recent", 144, 36, 4210752);
        drawCenteredString(fontRendererObj, page + "/" + total, 33, 145, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                mc.displayGuiScreen(new GuiListSearch(this, inventorySlots.getSlot(0).getStack()));
                return;
            case 2:
                mc.displayGuiScreen(new GuiEditor(this, null));
                return;
            case 3:
                mc.displayGuiScreen(new GuiListViewer(this));
                return;
            case 5:
                ++page;
                updateLayout();
                return;
            case 6:
                --page;
                updateLayout();
                return;
            case 7:
                mode = EnumMode.values()[Utilities.circulate(mode.ordinal(), 4, true)];
                updateLayout();
        }
    }

    @Nullable
    @Override
    protected String getButtonTooltip(int buttonId) {
        switch (buttonId) {
            case 4:
                return "Hello\nWorld";
            default:
                return null;
        }
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
        updateResult();
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
            updateResult();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textFieldAmount.mouseClicked(mouseX, mouseY, mouseButton);
        if (buttonMode.isMouseOver() && mouseButton == 1) {
            mode = EnumMode.values()[Utilities.circulate(mode.ordinal(), 4, false)];
            updateLayout();
            mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void onItemStackPick(ItemStack itemStack) {
        inventorySlots.getSlot(0).putStack(itemStack);
        onItemStackSet(0);
    }

    @Override
    protected void updateLayout() {
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

    void updateResult() {
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
                mc.thePlayer.addChatMessage(new TextComponentString("Sorry, an exception is caught during the calculation."));
                mc.thePlayer.addChatMessage(new TextComponentString("If you are using the latest version, you can report this issue to the author"));
                e.printStackTrace();
            }
        } else {
            calculator = null;
        }
        updateLayout();
    }

    public enum EnumMode {
        INPUT, PROCEDURE, OUTPUT, CATALYST;

        public String getDisplay() {
            switch (this) {
                case INPUT:
                    return "Input";
                case PROCEDURE:
                    return "Procedure";
                case OUTPUT:
                    return "Output";
                case CATALYST:
                    return "Catalyst";
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
