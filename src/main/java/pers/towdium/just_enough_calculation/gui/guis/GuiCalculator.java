package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.plugin.JEIPlugin;

import javax.annotation.Nullable;
import java.io.IOException;

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
                mc.displayGuiScreen(new GuiRecipeSearch(this));
                return;
            case 2:
                mc.displayGuiScreen(new GuiEditor(this));
                return;
            case 3:
                mc.displayGuiScreen(new GuiRecipeViewer(this));
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
    protected void onItemStackSet(int index) {
        ItemStack itemStack = inventorySlots.getSlot(index).getStack();
        if (itemStack != null && itemStack.getTagCompound() != null) {
            itemStack.getTagCompound().removeTag(JustEnoughCalculation.Reference.MODID);
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean handleMouseEvent(int mouseX, int mouseY) {
        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            if (activeSlot == -1) {
                Slot slot = getSlotUnderMouse();
                if (slot != null) {
                    switch (((JECContainer) inventorySlots).getSlotType(slot.getSlotIndex())) {
                        case SELECT:
                            temp = slot.getStack();
                            if (setActiveSlot(slot.getSlotIndex())) {
                                mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                            }
                            break;
                        case AMOUNT:
                            temp = slot.getStack();
                            if (!slot.getHasStack() && setActiveSlot(slot.getSlotIndex())) {
                                mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                            }
                            break;
                    }
                }
                return false;
            } else {
                Slot active = inventorySlots.getSlot(activeSlot);
                active.putStack(active.getStack());
                onItemStackSet(activeSlot);
                activeSlot = -1;
                mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                return true;
            }
        } else if (activeSlot != -1) {
            ItemStack stack = JEIPlugin.runtime.getItemListOverlay().getStackUnderMouse();
            inventorySlots.getSlot(activeSlot).putStack(stack == null ? null : stack.copy());
            return false;
        }
        return false;
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
            return index == 0 ? EnumSlotType.SELECT : EnumSlotType.DISABLED;
        }
    }
}
