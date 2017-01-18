package pers.towdium.just_enough_calculation.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.Arrays;

/**
 * Author: towdium
 * Date:   18/01/17
 */
public class JECGuiButton extends GuiButtonExt {
    JECGuiContainer gui;
    String name;
    boolean hasTooltip;
    long timeStartToolTip = 0;

    public JECGuiButton(int id, int xPos, int yPos, int width, int height, String name, JECGuiContainer gui) {
        this(id, xPos, yPos, width, height, name, gui, false, true);
    }

    public JECGuiButton(int id, int xPos, int yPos, int width, int height, String name, JECGuiContainer gui, boolean hasTooltip, boolean needLocalization) {
        super(id, xPos, yPos, width, height, needLocalization ? gui.localization(name) : name);
        this.gui = gui;
        this.name = name;
        this.hasTooltip = hasTooltip;
    }

    static JECGuiButton toJECGuiButton(GuiButton button, JECGuiContainer gui) {
        return button instanceof JECGuiButton ? ((JECGuiButton) button) : new JECGuiButton(
                button.id, button.xPosition, button.yPosition,
                button.width, button.height, button.displayString, gui);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);
        if (isMouseOverQuestion(mouseX, mouseY)) {
            if (timeStartToolTip == 0) {
                timeStartToolTip = System.currentTimeMillis();
            }
        } else {
            timeStartToolTip = 0;
        }
    }

    public void drawToolTip(int mouseX, int mouseY) {
        if (hasTooltip && timeStartToolTip != 0 && System.currentTimeMillis() - timeStartToolTip > 1000) {
            gui.drawHoveringText(Arrays.asList(gui.localizationToolTip(name).split("\\n")), mouseX, mouseY);
        }
    }

    public void drawOverlay(Minecraft mc, int mouseX, int mouseY) {
        int strWidth = mc.fontRendererObj.getStringWidth(displayString);
        int ellipsisWidth = mc.fontRendererObj.getStringWidth("...");
        int color = 14737632;
        if (packedFGColour != 0) {
            color = packedFGColour;
        } else if (!this.enabled) {
            color = 10526880;
        } else if (this.hovered) {
            color = 16777120;
        }

        if (strWidth > width - 6 && strWidth > ellipsisWidth && hovered) {
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.xPosition - (strWidth + 10 - width) / 2, this.yPosition, 0, 46 + getHoverState(true) * 20, strWidth + 10, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            drawCenteredString(mc.fontRendererObj, displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, color);
        }
    }

    public boolean isMouseOverQuestion(int mouseX, int mouseY) {
        return mouseX >= xPosition + width - 10 && mouseX <= xPosition + width
                && mouseY >= yPosition && mouseY <= yPosition + 10;
    }

    public boolean hasTooltip() {
        return hasTooltip;
    }
}
