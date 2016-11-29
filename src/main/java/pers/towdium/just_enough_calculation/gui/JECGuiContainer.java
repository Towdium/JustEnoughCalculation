package pers.towdium.just_enough_calculation.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ModelManager;
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

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Author:  Towdium
 * Created: 2016/6/13.
 */
public abstract class JECGuiContainer extends GuiContainer {
    static final String PREFIX = "gui.";
    public static GuiScreen lastGui;
    static Map<String, Map<String, String>> keyCache = new HashMap<>();
    static ModelManager tempMM = Utilities.getField(Minecraft.getMinecraft(), "modelManager", "field_175617_aL");
    protected GuiScreen parent;
    protected int activeSlot = -1;
    protected ItemStack temp;
    long timeStart = 0;

    public JECGuiContainer(Container inventorySlotsIn, GuiScreen parent) {
        super(inventorySlotsIn);
        this.parent = parent;
    }

    public static String localization(Class c, String translateKey, Object... parameters) {
        String name = c.getName();
        Map<String, String> map = keyCache.get(name);
        String key = map != null ? map.get(translateKey) : null;
        if (key == null) {
            StringBuilder builder = new StringBuilder(c.getName());
            builder.delete(0, builder.lastIndexOf(".") + 4).setCharAt(0, Character.toLowerCase(builder.charAt(0)));
            builder.insert(0, PREFIX).append('.').append(translateKey);
            key = builder.toString();
            if (map == null) {
                map = new HashMap<>();
                keyCache.put(name, map);
            }
            map.put(translateKey, key);
        }
        return LocalizationHelper.format(key, parameters);
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
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        RenderHelper.disableStandardItemLighting();
        drawTooltipScreen(p_73863_1_, p_73863_2_);
        RenderHelper.enableStandardItemLighting();
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
        //JustEnoughCalculation.log.info(typedChar + " " + keyCode);
    }

    @SuppressWarnings("NullableProblems")
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, mouseButton, type, mc.thePlayer);
        onItemStackSet(slotId);
    }

    @SuppressWarnings("ConstantConditions")
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
                //Slot active = inventorySlots.getSlot(activeSlot);
                //active.putStack(ItemStackHelper.toItemStackJEC(active.getStack()));
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

    protected void putStacks(int start, int end, List<ItemStack> stacks, int index) {
        for (int i = start; i <= end; i++) {
            int pos = index + i - start;
            inventorySlots.getSlot(i).putStack(stacks.size() > pos ? stacks.get(pos) : null);
        }
    }

    // Code get from source code of Refined Storage
    public void drawQuantity(int x, int y, String qty) {
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
        fontRendererObj.drawStringWithShadow(
                qty, (fontRendererObj.getUnicodeFlag() ? 16 : 30) - fontRendererObj.getStringWidth(qty),
                fontRendererObj.getUnicodeFlag() ? 8 : 22, 16777215
        );
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    // Methods to be overridden
    @Nullable
    protected abstract String getButtonTooltip(int buttonId);

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

    public String localization(String translateKey, Object... parameters) {
        return localization(this.getClass(), translateKey, parameters);
    }
}
