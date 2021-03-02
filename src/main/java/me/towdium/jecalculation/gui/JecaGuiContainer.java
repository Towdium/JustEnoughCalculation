package me.towdium.jecalculation.gui;

import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.polyfill.SoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public abstract class JecaGuiContainer extends GuiContainer {
    GuiScreen parent;
    int activeSlot = -1;
    long timeStart = 0;
    ItemStack temp;

    public JecaGuiContainer(Container inventorySlotsIn, GuiScreen parent) {
        super(inventorySlotsIn);
        this.parent = parent;
    }

    public boolean setActiveSlot(int id) {
        if (id == -1 || ((JecaContainer) inventorySlots).getSlotType(id) != JecaContainer.EnumSlotType.DISABLED) {
            activeSlot = id;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        //TODO check
        itemRender = new JecaRenderItem();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.disableStandardItemLighting(); // ?

        drawTooltipScreen(mouseX, mouseY);
        RenderHelper.enableStandardItemLighting(); // >
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (activeSlot != -1) {
            drawSlotOverlay(activeSlot, 0x60aeff00);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            if (activeSlot != -1) {
                inventorySlots.getSlot(activeSlot).putStack(temp);
                setActiveSlot(-1);
            } else {
                mc.displayGuiScreen(parent);
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int i1, int i2, int i3) {
        if (activeSlot == -1) {
            Slot slot = getSlotUnderMouse();
            if (slot != null) {
                switch (((JecaContainer) inventorySlots).getSlotType(slot.getSlotIndex())) {
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
            super.mouseClicked(i1, i2, i3);
        } else {
            Slot active = inventorySlots.getSlot(activeSlot);
            active.putStack(ItemStackHelper.toItemStackJEC(active.getStack()));
            activeSlot = -1;
            mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
        }
    }

    /**
     * called by NEI when need display item name or tool tip
     *
     * @param stack current mouse over item
     */
    public void handleMouseOverNEIItemPanel(ItemStack stack) {
        if (activeSlot != -1) {
            inventorySlots.getSlot(activeSlot)
                          .putStack(stack == null ? null : ItemStackHelper.toItemStackJEC(stack.copy()));
        }
    }

    protected void drawTooltipScreen(int mouseX, int mouseY) {
        boolean flagUnicode = mc.fontRenderer.getUnicodeFlag();
        boolean flagOver = false;
        mc.fontRenderer.setUnicodeFlag(true);
        //noinspection unchecked
        for (GuiButton button : (List<GuiButton>) buttonList) {
            String tooltip = getButtonTooltip(button.id);
            if (tooltip != null) {
                mc.fontRenderer
                        .drawStringWithShadow("?", button.xPosition + button.getButtonWidth() - 7, button.yPosition + 1,
                                              0xE0E0E0);
                if (isMouseOver(button, mouseX, mouseY)) {
                    flagOver = true;
                    mc.fontRenderer.drawStringWithShadow("\247b?", button.xPosition + button.getButtonWidth() - 7,
                                                         button.yPosition + 1, 16777215);
                    mc.fontRenderer.setUnicodeFlag(flagUnicode);
                    if (timeStart == 0) {
                        timeStart = System.currentTimeMillis();
                    } else if (System.currentTimeMillis() - timeStart > 1000) {
                        drawHoveringText(Arrays.asList(tooltip.split("\\\\n")), mouseX, mouseY, mc.fontRenderer);
                    }
                }
            }
        }
        if (!flagOver) {
            timeStart = 0;
            mc.fontRenderer.setUnicodeFlag(flagUnicode);
        }
    }

    protected void drawSlotOverlay(int index, int color) {
        Slot slot = inventorySlots.getSlot(index);
        int size = getSizeSlot(index);
        int move = (size - 16) / 2;
        drawRect(slot.xDisplayPosition - move, slot.yDisplayPosition - move, slot.xDisplayPosition + size - move,
                 slot.yDisplayPosition + size - move, color);
    }

    public void drawCenteredStringMultiLine(FontRenderer fontRendererIn,
                                            String text,
                                            int x1,
                                            int x2,
                                            int y1,
                                            int y2,
                                            int color) {
        String[] buffer = text.split("\\n");
        float x = (x1 + x2) / 2.0f;
        float y = (y1 + y2 - buffer.length * 10 + 2) / 2.0f - 10;
        for (String s : buffer) {
            fontRendererIn
                    .drawStringWithShadow(s, (int) x - fontRendererIn.getStringWidth(s) / 2, (int) (y += 10), color);
        }
    }

    protected boolean isMouseOver(GuiButton button, int mouseX, int mouseY) {
        return mouseX >= button.xPosition + button.getButtonWidth() - 10 &&
               mouseX <= button.xPosition + button.getButtonWidth() && mouseY >= button.yPosition &&
               mouseY <= button.yPosition + 10;
    }

    protected void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
    }

    protected abstract String getButtonTooltip(int buttonId);

    protected abstract int getSizeSlot(int index);

    protected Slot getSlotUnderMouse() {
        Slot theSlot = null;
        try {
            Field field = JecaGuiContainer.class.getSuperclass().getDeclaredField("theSlot");
            field.setAccessible(true);
            theSlot = (Slot) field.get(this);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return theSlot;
    }

    static class RenderItemSupplier implements Supplier<RenderItem> {
        @Override
        public RenderItem get() {
            return null;
        }
    }
}
