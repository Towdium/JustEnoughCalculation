package pers.towdium.just_enough_calculation.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import pers.towdium.just_enough_calculation.plugin.JEIPlugin;
import pers.towdium.just_enough_calculation.util.Utilities;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper.NBT;
import pers.towdium.just_enough_calculation.util.helpers.LocalizationHelper;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Author:  Towdium
 * Created: 2016/6/13.
 */
public abstract class JECGuiContainer extends GuiContainer {
    static final String PREFIX = "gui.";
    public static GuiScreen lastGui;

    protected GuiScreen parent;
    protected int activeSlot = -1;
    protected ItemStack temp;

    public JECGuiContainer(Container inventorySlotsIn, GuiScreen parent) {
        super(inventorySlotsIn);
        this.parent = parent;
        labelList.add(new MyLabel(fontRendererObj));
    }

    public String localization(String translateKey, Object... parameters) {
        return LocalizationHelper.localization(this.getClass(), PREFIX, translateKey, parameters).one;
    }

    public String localizationToolTip(String translateKey, Object... parameters) {
        return localization("tooltip." + translateKey, parameters);
    }

    public boolean setActiveSlot(int id) {
        if (id == -1 || ((JECContainer) inventorySlots).getSlotType(id) != JECContainer.EnumSlotType.DISABLED) {
            activeSlot = id;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        init();
        lastGui = this;
        updateLayout();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (activeSlot != -1) {
            drawSlotOverlay(activeSlot, 0x60aeff00);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        inventorySlots.inventorySlots.forEach(slot -> {
            ItemStack s = slot.getStack();
            if (s != null)
                drawQuantity(
                        slot.xDisplayPosition, slot.yDisplayPosition,
                        getFormer().apply(NBT.getAmount(s), NBT.getType(s))
                );
        });
        drawTooltipScreen(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            if (activeSlot != -1) {
                inventorySlots.getSlot(activeSlot).putStack(temp);
                setActiveSlot(-1);
                mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
            } else {
                lastGui = parent;
                Utilities.openGui(parent);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        buttonList.forEach((button -> JECGuiButton.toJECGuiButton(button, this).drawToolTip(mouseX, mouseY)));
    }

    @Override
    public void drawHoveringText(List<String> textLines, int x, int y) {
        super.drawHoveringText(textLines, x, y);
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }

    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, mouseButton, type, mc.thePlayer);
        onItemStackSet(slotId);
    }

    public boolean handleMouseEvent() {
        if (Mouse.getEventDWheel() != 0) {
            Slot slot = getSlotUnderMouse();
            if (slot != null && ((JECContainer) inventorySlots).getSlotType(slot.getSlotIndex()) == JECContainer.EnumSlotType.AMOUNT) {
                ItemStack stack = null;
                boolean up = Mouse.getEventDWheel() > 0;
                boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
                if (up && shift) {
                    stack = ItemStackHelper.Click.leftShift(slot.getStack());
                } else if (up && !shift) {
                    stack = ItemStackHelper.Click.leftClick(slot.getStack());
                } else if (!up && shift) {
                    stack = ItemStackHelper.Click.rightShift(slot.getStack());
                } else if (!up && !shift) {
                    stack = ItemStackHelper.Click.rightClick(slot.getStack());
                }
                if (stack == null) {
                    slot.putStack(null);
                }
                onItemStackSet(slot.getSlotIndex());
            }
        } else if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
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
                        case PICKER:
                            if (slot.getHasStack()) {
                                onItemStackPick(slot.getStack());
                                mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                            }
                    }
                }
                return false;
            } else {
                onItemStackSet(activeSlot);
                activeSlot = -1;
                mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                return true;
            }
        } else if (activeSlot != -1) {
            ItemStack stack = JEIPlugin.runtime.getItemListOverlay().getStackUnderMouse();
            stack = stack == null ? (getSlotUnderMouse() != null ? getSlotUnderMouse().getStack() : null) : stack;
            inventorySlots.getSlot(activeSlot).putStack(stack == null ? null :
                    ((JECContainer) inventorySlots).getSlotType(activeSlot) == JECContainer.EnumSlotType.AMOUNT ?
                            ItemStackHelper.toItemStackJEC(stack.copy()) : stack.copy());
            return false;
        }
        return false;
    }

    protected void drawTooltipScreen(int mouseX, int mouseY) {
        for (GuiButton button : buttonList) {
            JECGuiButton b = JECGuiButton.toJECGuiButton(button, this);
            if (b.shouldDrawQuestion()) {
                int drawX = b.xPosition + b.getButtonWidth() - 6 - guiLeft;
                int drawY = b.yPosition + 3 - guiTop;
                if (b.isMouseOverQuestion(mouseX, mouseY)) {
                    drawInHalfSize(drawX, drawY, () -> drawString(fontRendererObj, "\247b?", 0, 0, 16777215));
                } else {
                    drawInHalfSize(drawX, drawY, () -> drawString(fontRendererObj, "?", 0, 0, 14737632));
                }
            }
        }
    }

    protected void drawSlotOverlay(int index, int color) {
        Slot slot = inventorySlots.getSlot(index);
        int size = getSizeSlot(index);
        int move = (size - 16) / 2;
        drawRect(slot.xDisplayPosition - move, slot.yDisplayPosition - move, slot.xDisplayPosition + size - move, slot.yDisplayPosition + size - move, color);
    }

    protected void drawInHalfSize(int x, int y, Runnable r) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 1);
        if (!fontRendererObj.getUnicodeFlag()) {
            GlStateManager.scale(0.5f, 0.5f, 1);
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        r.run();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void drawCenteredStringMultiLine(FontRenderer fontRendererIn, String text, int x1, int x2, int y1, int y2, int color) {
        String[] buffer = text.split("\\n");
        float x = (x1 + x2) / 2.0f;
        float y = (y1 + y2 - buffer.length * 10 + 2) / 2.0f - 10;
        for (String s : buffer) {
            fontRendererIn.drawStringWithShadow(s, x - fontRendererIn.getStringWidth(s) / 2, y += 10, color);
        }
    }

    protected void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
    }

    protected void putStacks(int start, int end, List<ItemStack> stacks, int index) {
        for (int i = start; i <= end; i++) {
            int pos = index + i - start;
            inventorySlots.getSlot(i).putStack(stacks.size() > pos ? stacks.get(pos) : null);
        }
    }

    // Code get from source code of Refined Storage
    public void drawQuantity(int x, int y, String qty) {
        drawInHalfSize(x, y, () ->
                fontRendererObj.drawStringWithShadow(
                        qty, (fontRendererObj.getUnicodeFlag() ? 16 : 30) - fontRendererObj.getStringWidth(qty),
                        fontRendererObj.getUnicodeFlag() ? 8 : 22, 16777215
                )
        );
    }

    public FontRenderer getFontRenderer() {
        return fontRendererObj;
    }

    protected abstract int getSizeSlot(int index);

    protected abstract void init();

    public void onItemStackSet(int index) {
    }

    public void updateLayout() {
    }

    protected void onItemStackPick(ItemStack itemStack) {
    }

    protected BiFunction<Long, ItemStackHelper.EnumStackAmountType, String> getFormer() {
        return (aLong, type) -> type.getStringEditor(aLong);
    }

    class MyLabel extends GuiLabel {

        public MyLabel(FontRenderer fontRendererObj) {
            super(fontRendererObj, 0, 0, 0, 0, 0, 0);
        }

        @Override
        public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
            JECGuiContainer.this.buttonList.forEach((button -> JECGuiButton.toJECGuiButton(button, JECGuiContainer.this).drawOverlay(mc)));
        }
    }
}
