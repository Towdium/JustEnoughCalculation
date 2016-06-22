package pers.towdium.justEnoughCalculation.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import pers.towdium.justEnoughCalculation.plugin.JEIPlugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author:  Towdium
 * Created: 2016/6/13.
 */
public abstract class JECGuiContainer extends GuiContainer {
    GuiScreen parent;
    int activeSlot = -1;
    long timeStart = 0;

    public JECGuiContainer(Container inventorySlotsIn, GuiScreen parent) {
        super(inventorySlotsIn);
        this.parent = parent;
    }

    public boolean setActiveSlot(int id){
        if(id == -1 || getSizeSlotActive(id) != 0){
            activeSlot = id;
            return true;
        } else {
            return false;
        }
    }

    public int getActiveSlot(){
        return activeSlot;
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
        drawActiveSlot();
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1){
            if(activeSlot != -1){
                setActiveSlot(-1);
            } else {
                mc.displayGuiScreen(parent);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public boolean handleMouseEvent(int mouseX, int mouseY){
        if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState()){
            if(activeSlot == -1){
                Slot slot = getSlotUnderMouse();
                if(slot != null && setActiveSlot(slot.getSlotIndex())){
                    mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                }
                return false;
            } else {
                activeSlot = -1;
                mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
                return true;
            }
        } else if(activeSlot != -1){
            ItemStack stack = JEIPlugin.runtime.getItemListOverlay().getStackUnderMouse();
            inventorySlots.getSlot(activeSlot).putStack(stack == null ? null : stack.copy());
            return false;
        }
        return false;
    }

    protected void drawTooltipScreen(int mouseX, int mouseY){
        boolean flagUnicode = mc.fontRendererObj.getUnicodeFlag();
        boolean flagOver = false;
        mc.fontRendererObj.setUnicodeFlag(true);
        for (GuiButton button : buttonList) {
            String tooltip = getButtonTooltip(button.id);
            if (tooltip != null) {
                mc.fontRendererObj.drawStringWithShadow("?", button.xPosition + button.getButtonWidth() - 7, button.yPosition + 1, 14737632);
                if(isMouseOver(button, mouseX, mouseY)){
                    flagOver = true;
                    mc.fontRendererObj.drawStringWithShadow("\247b?", button.xPosition+button.getButtonWidth()-7, button.yPosition+1, 16777215);
                    mc.fontRendererObj.setUnicodeFlag(flagUnicode);
                    if(timeStart == 0){
                        timeStart = System.currentTimeMillis();
                    } else if(System.currentTimeMillis()-timeStart > 1000){
                        drawHoveringText(Arrays.asList(tooltip.split("\\\\n")), mouseX, mouseY);
                    }
                }
            }
        }
        if(!flagOver){
            timeStart = 0;
            mc.fontRendererObj.setUnicodeFlag(flagUnicode);
        }
    }

    protected void drawActiveSlot(){
        if(activeSlot != -1){
            Slot slot = inventorySlots.getSlot(activeSlot);
            int size = getSizeSlotActive(activeSlot);
            int move = (size-16)/2;
            drawRect(slot.xDisplayPosition-move, slot.yDisplayPosition-move, slot.xDisplayPosition+size-move, slot.yDisplayPosition+size-move, 0x60aeff00);
        }
    }

    public void drawCenteredStringMultiLine(FontRenderer fontRendererIn, String text, int x1, int x2, int y1, int y2, int color) {
        String[] buffer = text.split("\\n");
        float x = (x1+x2)/2.0f;
        float y = (y1+y2-buffer.length*10+2)/2.0f-10;
        for(String s : buffer){
            fontRendererIn.drawStringWithShadow(s, x-fontRendererIn.getStringWidth(s)/2, y+=10, color);
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

    protected abstract int getSizeSlotActive(int index);
}
