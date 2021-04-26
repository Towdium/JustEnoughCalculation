package me.towdium.jecalculation.client.widget.widgets;

import cpw.mods.fml.client.config.GuiButtonExt;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.widget.Widget;
import me.towdium.jecalculation.utils.ClientUtils;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
public class WButton extends Widget.Advanced {
    public int xPos, yPos, xSize, ySize;
    public Runnable lsnrLeft, lsnrRight;
    public String text, tooltip;
    protected GuiButtonExt button;
    protected Utilities.Timer timer = new Utilities.Timer();

    public WButton(int xPos, int yPos, int xSize, int ySize, String text) {
        this(xPos, yPos, xSize, ySize, text, null);
    }

    public WButton(int xPos, int yPos, int xSize, int ySize, String text, @Nullable String tooltip) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.text = text;
        this.tooltip = tooltip;
    }

    public WButton setListenerLeft(Runnable r) {
        lsnrLeft = r;
        return this;
    }

    public WButton setListenerRight(Runnable r) {
        lsnrRight = r;
        return this;
    }

    protected static boolean mouseIn(GuiButtonExt button, int x, int y) {
        return button.enabled && button.visible &&
               JecGui.mouseIn(button.xPosition, button.yPosition, button.width, button.height, x, y);
    }


    @Override
    public void onGuiInit(JecGui gui) {
        super.onGuiInit(gui);
        button = new GuiButtonExt(0, xPos + gui.getGuiLeft(), yPos + gui.getGuiTop(), xSize, ySize, text) {
            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                return false;
            }
        };
        gui.buttonList.add(button);
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        if (mouseIn(this.button, xMouse, yMouse)) {
            if (button == 0 && lsnrLeft != null) {
                lsnrLeft.run();
                ClientUtils.playClickSound(1.0F);
                return true;
            } else if (button == 1 && lsnrRight != null) {
                lsnrLeft.run();
                ClientUtils.playClickSound(0.8F);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRemoved(JecGui gui) {
        gui.buttonList.remove(button);
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        if (tooltip != null) {
            boolean hovered = JecGui.mouseIn(xPos + gl(gui), yPos + gt(gui), xSize, ySize, xMouse, yMouse);
            timer.setState(hovered);
            if (timer.getTime() > 500) gui.drawTooltip(xMouse, yMouse, gui.localize("tooltip." + tooltip));
        }
    }
}
