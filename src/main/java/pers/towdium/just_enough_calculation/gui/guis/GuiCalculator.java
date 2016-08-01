package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.core.Calculator;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;

import javax.annotation.Nullable;
import java.io.IOException;
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
        buttonMode = new GuiButton(7, guiLeft + 63, guiTop + 139, 52, 20, "Catalyst");
        buttonStock = new GuiButton(8, guiLeft + 119, guiTop + 139, 50, 20, "Stock");
        buttonList.add(buttonSearch);
        buttonList.add(buttonAdd);
        buttonList.add(buttonView);
        buttonList.add(buttonSettings);
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        buttonList.add(buttonMode);
        buttonList.add(buttonStock);
        textFieldAmount = new GuiTextField(0, fontRendererObj, guiLeft + 39, guiTop + 8, 75, 18);
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
        Calculator calculator = new Calculator(inventorySlots.getSlot(0).getStack(), 10);
    }

    @Override
    protected void onItemStackPick(ItemStack itemStack) {
        inventorySlots.getSlot(0).putStack(itemStack);
        onItemStackSet(0);
    }

    public enum EnumMode {
        INPUT, PROCEDURE, OUTPUT, CATALYST;

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
