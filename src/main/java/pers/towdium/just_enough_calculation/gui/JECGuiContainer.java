package pers.towdium.just_enough_calculation.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import pers.towdium.just_enough_calculation.plugin.JEIPlugin;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.ReflectionHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Author:  Towdium
 * Created: 2016/6/13.
 */
public abstract class JECGuiContainer extends GuiContainer {
    protected GuiScreen parent;
    protected int activeSlot = -1;
    protected ItemStack temp;
    long timeStart = 0;

    public JECGuiContainer(Container inventorySlotsIn, GuiScreen parent) {
        super(inventorySlotsIn);
        this.parent = parent;
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
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        ModelManager tempMM = ReflectionHelper.getField(mc, "modelManager", "field_175617_aL");
        if (tempMM != null) {
            itemRender = new RenderItem(mc.getTextureManager(), tempMM, mc.getItemColors()) {
                @Override
                public void renderItemOverlayIntoGUI(@SuppressWarnings("NullableProblems") FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
                    boolean b = fr.getUnicodeFlag();
                    fr.setUnicodeFlag(true);
                    super.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, stack == null ? "" : ItemStackHelper.NBT.getType(stack).getDisplayString(ItemStackHelper.NBT.getAmount(stack)));
                    fr.setUnicodeFlag(b);
                }
            };
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.disableStandardItemLighting();

        drawTooltipScreen(mouseX, mouseY);
        RenderHelper.enableStandardItemLighting();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (activeSlot != -1) {
            drawSlotOverlay(activeSlot, 0x60aeff00);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            if (activeSlot != -1) {
                inventorySlots.getSlot(activeSlot).putStack(temp);
                setActiveSlot(-1);
            } else {
                mc.displayGuiScreen(parent);
            }
        }
    }

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
                active.putStack(ItemStackHelper.toItemStackJEC(active.getStack()));
                onItemStackSet(activeSlot);
                activeSlot = -1;
                mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                return true;
            }
        } else if (activeSlot != -1) {
            ItemStack stack = JEIPlugin.runtime.getItemListOverlay().getStackUnderMouse();
            inventorySlots.getSlot(activeSlot).putStack(stack == null ? null : ItemStackHelper.toItemStackJEC(stack.copy()));
            return false;
        }
        return false;
    }

    protected void drawTooltipScreen(int mouseX, int mouseY) {
        boolean flagUnicode = mc.fontRendererObj.getUnicodeFlag();
        boolean flagOver = false;
        mc.fontRendererObj.setUnicodeFlag(true);
        for (GuiButton button : buttonList) {
            String tooltip = getButtonTooltip(button.id);
            if (tooltip != null) {
                mc.fontRendererObj.drawStringWithShadow("?", button.xPosition + button.getButtonWidth() - 7, button.yPosition + 1, 14737632);
                if (isMouseOver(button, mouseX, mouseY)) {
                    flagOver = true;
                    mc.fontRendererObj.drawStringWithShadow("\247b?", button.xPosition + button.getButtonWidth() - 7, button.yPosition + 1, 16777215);
                    mc.fontRendererObj.setUnicodeFlag(flagUnicode);
                    if (timeStart == 0) {
                        timeStart = System.currentTimeMillis();
                    } else if (System.currentTimeMillis() - timeStart > 1000) {
                        drawHoveringText(Arrays.asList(tooltip.split("\\n")), mouseX, mouseY);
                    }
                }
            }
        }
        if (!flagOver) {
            timeStart = 0;
            mc.fontRendererObj.setUnicodeFlag(flagUnicode);
        }
    }

    protected void drawSlotOverlay(int index, int color) {
        Slot slot = inventorySlots.getSlot(index);
        int size = getSizeSlot(index);
        int move = (size - 16) / 2;
        drawRect(slot.xDisplayPosition - move, slot.yDisplayPosition - move, slot.xDisplayPosition + size - move, slot.yDisplayPosition + size - move, color);
    }

    public void drawCenteredStringMultiLine(FontRenderer fontRendererIn, String text, int x1, int x2, int y1, int y2, int color) {
        String[] buffer = text.split("\\n");
        float x = (x1 + x2) / 2.0f;
        float y = (y1 + y2 - buffer.length * 10 + 2) / 2.0f - 10;
        for (String s : buffer) {
            fontRendererIn.drawStringWithShadow(s, x - fontRendererIn.getStringWidth(s) / 2, y += 10, color);
        }
    }

    protected boolean isMouseOver(GuiButton button, int mouseX, int mouseY) {
        return mouseX >= button.xPosition + button.getButtonWidth() - 10
                && mouseX <= button.xPosition + button.getButtonWidth()
                && mouseY >= button.yPosition
                && mouseY <= button.yPosition + 10;
    }

    protected void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
    }

    // Methods to be overridden
    @Nullable
    protected abstract String getButtonTooltip(int buttonId);

    protected abstract int getSizeSlot(int index);

    protected void onItemStackSet(int index) {
    }

    protected void updateLayout() {
    }

    static class RenderItemSupplier implements Supplier<RenderItem> {
        @Override
        public RenderItem get() {
            return null;
        }
    }
}
