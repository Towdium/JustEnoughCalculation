package me.towdium.jecalculation.gui;

import me.towdium.jecalculation.plugin.JEIPlugin;
import me.towdium.jecalculation.util.Utilities;
import me.towdium.jecalculation.util.helpers.ItemStackHelper;
import me.towdium.jecalculation.util.helpers.ItemStackHelper.NBT;
import me.towdium.jecalculation.util.helpers.LocalizationHelper;
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
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

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
        labelList.add(new MyLabel(fontRenderer));
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
            if (!s.isEmpty())
                drawQuantity(slot.xPos, slot.yPos,
                        getFormer(slot.getSlotIndex()).apply(NBT.getAmount(s), NBT.getType(s)));
        });
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            if (activeSlot != -1) {
                inventorySlots.getSlot(activeSlot).putStack(temp);
                setActiveSlot(-1);
                mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
            } else {
                lastGui = parent;
                Utilities.openGui(parent);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        buttonList.forEach((button -> toJECGuiButton(button).drawToolTip(mouseX, mouseY)));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttonList.forEach(button -> {
            JECGuiButton jecButton = toJECGuiButton(button);
            jecButton.mouseClicked(mouseX, mouseY, mouseButton);
        });
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawHoveringText(List<String> textLines, int x, int y) {
        super.drawHoveringText(textLines, x, y);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        ItemStack s = slotIn == null ? ItemStack.EMPTY : slotIn.getStack();
        ItemStack r = inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, mouseButton, type, mc.player);
        if ((!s.isEmpty() && !s.equals(r)) || (s.isEmpty() && !r.isEmpty())) {
            onItemStackSet(slotId, null);
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        if (Keyboard.getEventKeyState()) {
            int key = Keyboard.getEventKey();
            char ch = Keyboard.getEventCharacter();
            buttonList.forEach((button -> toJECGuiButton(button).keyTyped(key, ch)));
        }
    }

    public boolean handleMouseEvent() {
        if (Mouse.getEventDWheel() != 0) {
            Slot slot = getSlotUnderMouse();
            //noinspection ConstantConditions
            if (slot != null && ((JECContainer) inventorySlots).getSlotType(slot.getSlotIndex()) == JECContainer.EnumSlotType.AMOUNT) {
                ItemStack stack;
                boolean up = Mouse.getEventDWheel() > 0;
                boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
                if (up && shift) {
                    stack = ItemStackHelper.Click.leftShift(slot.getStack());
                } else if (up) {
                    stack = ItemStackHelper.Click.leftClick(slot.getStack());
                } else if (shift) {
                    stack = ItemStackHelper.Click.rightShift(slot.getStack());
                } else {
                    stack = ItemStackHelper.Click.rightClick(slot.getStack());
                }
                if (stack.isEmpty()) {
                    ItemStack buf = slot.getStack();
                    slot.putStack(ItemStack.EMPTY);
                    onItemStackSet(slot.getSlotIndex(), buf);
                }
            }
        }
        if (Mouse.getEventButtonState() && activeSlot == -1) {
            ItemStack stack = JEIPlugin.runtime.getItemListOverlay().getStackUnderMouse();
            int dest = getDestSlot(Mouse.getEventButton());
            if (dest != -1 && stack != null) {
                ItemStack s = inventorySlots.getSlot(dest).getStack();
                //noinspection ConstantConditions
                inventorySlots.getSlot(dest).putStack(stack == null ? null :
                        ((JECContainer) inventorySlots).getSlotType(dest) == JECContainer.EnumSlotType.AMOUNT ?
                                ItemStackHelper.toItemStackJEC(stack.copy()) : stack.copy());
                onItemStackSet(dest, s);
                return true;
            }
        }
        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            if (activeSlot == -1) {
                Slot slot = getSlotUnderMouse();
                //noinspection ConstantConditions
                if (slot != null) {
                    switch (((JECContainer) inventorySlots).getSlotType(slot.getSlotIndex())) {
                        case SELECT:
                            temp = slot.getStack();
                            if (setActiveSlot(slot.getSlotIndex())) {
                                mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                            }
                            break;
                        case AMOUNT:
                            temp = slot.getStack();
                            if (!slot.getHasStack() && setActiveSlot(slot.getSlotIndex())) {
                                mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                            }
                            break;
                        case PICKER:
                            if (slot.getHasStack()) {
                                onItemStackPick(slot.getStack());
                                mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                            }
                    }
                    return false;
                }
            } else {
                onItemStackSet(activeSlot, temp);
                activeSlot = -1;
                mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                return true;
            }
        }
        if (activeSlot != -1) {
            ItemStack stack = JEIPlugin.runtime.getItemListOverlay().getStackUnderMouse();
            if (stack == null) {
                stack = ItemStack.EMPTY;
            }
            //noinspection ConstantConditions
            stack = stack.isEmpty() ? (getSlotUnderMouse() != null ? getSlotUnderMouse().getStack() : ItemStack.EMPTY) : stack;
            inventorySlots.getSlot(activeSlot).putStack(stack.isEmpty() ? ItemStack.EMPTY :
                    ((JECContainer) inventorySlots).getSlotType(activeSlot) == JECContainer.EnumSlotType.AMOUNT ?
                            ItemStackHelper.toItemStackJEC(stack.copy()) : stack.copy());
            return false;
        }
        return false;
    }

    protected int getDestSlot(int button) {
        return -1;
    }

    protected void drawSlotOverlay(int index, int color) {
        Slot slot = inventorySlots.getSlot(index);
        int size = getSizeSlot(index);
        int move = (size - 16) / 2;
        drawRect(slot.xPos - move, slot.yPos - move, slot.xPos + size - move, slot.yPos + size - move, color);
    }

    protected void drawInHalfSize(int x, int y, Runnable r) {
        boolean b = fontRenderer.getUnicodeFlag();
        fontRenderer.setUnicodeFlag(false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 1);
        if (!fontRenderer.getUnicodeFlag()) {
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
        fontRenderer.setUnicodeFlag(b);
    }

    protected int getLastFilledSlot() {
        return getLastFilledSlot(-1);
    }

    protected int getLastFilledSlot(int start) {
        List<Slot> slots = inventorySlots.inventorySlots;
        for (int i = start < 0 ? slots.size() - 1 : start; i >= 0; i--) {
            if (slots.get(i) != null && slots.get(i).getHasStack())
                return i;
        }
        return -1;
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
            inventorySlots.getSlot(i).putStack(stacks.size() > pos ? stacks.get(pos) : ItemStack.EMPTY);
        }
    }

    // Code get from source code of Refined Storage
    public void drawQuantity(int x, int y, String qty) {
        drawInHalfSize(x, y, () ->
                fontRenderer.drawStringWithShadow(
                        qty, (fontRenderer.getUnicodeFlag() ? 16 : 30) - fontRenderer.getStringWidth(qty),
                        fontRenderer.getUnicodeFlag() ? 8 : 22, 16777215
                )
        );
    }

    protected abstract int getSizeSlot(int index);

    protected abstract void init();

    public void onItemStackSet(int index, ItemStack old) {
    }

    public void updateLayout() {
    }

    protected void onItemStackPick(ItemStack itemStack) {
    }

    protected BiFunction<Long, ItemStackHelper.EnumStackAmountType, String> getFormer(int id) {
        return (aLong, type) -> type.getStringEditor(aLong);
    }

    JECGuiButton toJECGuiButton(GuiButton button) {
        return button instanceof JECGuiButton ? ((JECGuiButton) button) : new JECGuiButton(
                button.id, button.x, button.y,
                button.width, button.height, button.displayString);
    }

    class MyLabel extends GuiLabel {

        public MyLabel(FontRenderer fontRendererObj) {
            super(fontRendererObj, 0, 0, 0, 0, 0, 0);
        }

        @Override
        public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
            JECGuiContainer.this.buttonList.forEach((button ->
                    toJECGuiButton(button).drawOverlay(mc, mouseX, mouseY)));
        }
    }

    public class JECGuiButton extends GuiButtonExt {
        String name;
        boolean hasTooltip;
        long timeStartToolTip = 0;
        long timeStartButton = 0;
        Runnable listenerLeft = null;
        Runnable listenerRight = null;
        BiPredicate<Integer, Character> keyAdapter = null;

        public JECGuiButton(int id, int xPos, int yPos, int width, int height, String name) {
            this(id, xPos, yPos, width, height, name, true, false);
        }

        public JECGuiButton(int id, int xPos, int yPos, int width, int height, String name, boolean needLocalization) {
            this(id, xPos, yPos, width, height, name, needLocalization, false);
        }

        public JECGuiButton(int id, int xPos, int yPos, int width, int height, String name,
                            boolean needLocalization, boolean hasTooltip) {
            super(id, xPos, yPos, width, height, needLocalization ? localization(name) : name);
            this.name = name;
            this.hasTooltip = hasTooltip;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
            if (keyAdapter != null && Keyboard.getEventKeyState() && keyAdapter.test(Keyboard.getEventKey(), Keyboard.getEventCharacter())) {
                super.drawButton(mc, x, y, partial);
            } else {
                super.drawButton(mc, mouseX, mouseY, partial);
            }
            if (hovered && !isMouseOverQuestion(mouseX, mouseY)) {
                if (timeStartButton == 0) {
                    timeStartButton = System.currentTimeMillis();
                }
            } else {
                timeStartButton = 0;
            }
            if (isMouseOverQuestion(mouseX, mouseY)) {
                if (timeStartToolTip == 0) {
                    timeStartToolTip = System.currentTimeMillis();
                }
            } else {
                timeStartToolTip = 0;
            }
        }

        public void drawToolTip(int mouseX, int mouseY) {
            if (hasTooltip && timeStartToolTip != 0 && System.currentTimeMillis() - timeStartToolTip > 600) {
                drawHoveringText(Arrays.asList(localizationToolTip(name).split("\\n")), mouseX, mouseY);
            }
        }

        public void drawOverlay(Minecraft mc, int mouseX, int mouseY) {
            if (shouldDrawQuestion()) {
                int drawX = x + getButtonWidth() - 6;
                int drawY = y + 3;
                if (isMouseOverQuestion(mouseX, mouseY)) {
                    drawInHalfSize(drawX, drawY, () -> drawString(fontRenderer, "\247b?", 0, 0, 16777215));
                } else {
                    drawInHalfSize(drawX, drawY, () -> drawString(fontRenderer, "?", 0, 0, 14737632));
                }
            }

            if (!shouldDrawOverlay()) {
                return;
            }

            int color = 14737632;
            if (packedFGColour != 0) {
                color = packedFGColour;
            } else if (!this.enabled) {
                color = 10526880;
            } else if (this.hovered) {
                color = 16777120;
            }
            int strWidth = fontRenderer.getStringWidth(displayString);
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES,
                    this.x - (strWidth + 10 - width) / 2, this.y, 0, 46 + getHoverState(true) * 20,
                    strWidth + 10, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            drawCenteredString(mc.fontRenderer, displayString,
                    this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
        }

        public boolean isMouseOverQuestion(int mouseX, int mouseY) {
            return mouseX >= x + width - 10 && mouseX <= x + width
                    && mouseY >= y && mouseY <= y + 10;
        }

        public boolean shouldDrawQuestion() {
            return !shouldDrawOverlay() && hasTooltip;
        }

        public boolean shouldDrawOverlay() {
            int strWidth = fontRenderer.getStringWidth(displayString);
            int ellipsisWidth = fontRenderer.getStringWidth("...");
            return strWidth > width - 6 && strWidth > ellipsisWidth && hovered &&
                    timeStartButton != 0 && System.currentTimeMillis() - timeStartButton > 600;
        }

        public void mouseClicked(int mouseX, int mouseY, int button) {
            if (this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y
                    && mouseX < this.x + this.width && mouseY < this.y + this.height) {
                if (button == 0 && listenerLeft != null) {
                    listenerLeft.run();
                } else if (button == 1 && listenerRight != null) {
                    listenerRight.run();
                    mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                }
            }
        }

        public void keyTyped(int key, char ch) {
            if (keyAdapter != null && keyAdapter.test(key, ch)) {
                listenerLeft.run();
            }
        }

        public JECGuiButton setKeyAdapter(BiPredicate<Integer, Character> adapter) {
            keyAdapter = adapter;
            return this;
        }

        public JECGuiButton setLsnLeft(Runnable r) {
            listenerLeft = r;
            return this;
        }

        public JECGuiButton setLsnRight(Runnable r) {
            listenerRight = r;
            return this;
        }
    }
}
