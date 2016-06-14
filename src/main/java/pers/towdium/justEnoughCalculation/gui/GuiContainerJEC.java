package pers.towdium.justEnoughCalculation.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;

/**
 * Author:  Towdium
 * Created: 2016/6/13.
 */
public abstract class GuiContainerJEC extends GuiContainer {
    GuiScreen parent;
    long timeStart = 0;

    public GuiContainerJEC(Container inventorySlotsIn, GuiScreen parent) {
        super(inventorySlotsIn);
        this.parent = parent;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.disableStandardItemLighting();
        drawTooltipScreen(mouseX, mouseY);
        RenderHelper.enableStandardItemLighting();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1){
            mc.displayGuiScreen(parent);
        }
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
    protected String getButtonTooltip(int buttonId){
        return null;
    }
}
