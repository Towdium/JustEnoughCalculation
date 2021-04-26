package me.towdium.jecalculation.client.widget.widgets;

import cpw.mods.fml.client.config.GuiButtonExt;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.resource.Resource;
import net.minecraft.client.Minecraft;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
public class WButtonIcon extends WButton {
    protected int xPos, yPos;
    protected Resource normal, focused;

    public WButtonIcon(int xPos, int yPos, Resource normal, Resource focused) {
        super(xPos, yPos, 20, 20, "");
        this.xPos = xPos;
        this.yPos = yPos;
        this.normal = normal;
        this.focused = focused;
    }

    @Override
    public void onGuiInit(JecGui gui) {
        super.onGuiInit(gui);
        gui.buttonList.add(new GuiButtonExt(0, xPos + gui.getGuiLeft(), yPos + gui.getGuiTop(), 20, 20, "") {
            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                final int x = this.xPosition;
                final int y = this.yPosition;
                this.field_146123_n = mouseX > x + 1 && mouseY > y + 1 && mouseX <= x + width - 1 && mouseY <= y + height - 1;
                boolean hovered = this.field_146123_n;
                gui.drawResource(hovered ? Resource.BUTTON_FOCUSED : Resource.BUTTON_NORMAL, x, y);
                String text = displayString;
                int strWidth = mc.fontRenderer.getStringWidth(text);
                int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
                if (strWidth > width - 6 && strWidth > ellipsisWidth)
                    text = mc.fontRenderer.trimStringToWidth(text, width - 6 - ellipsisWidth).trim() + "...";
                this.drawCenteredString(mc.fontRenderer, text, x + this.width / 2, y + (this.height - 8) / 2,
                        hovered ? 16777120 : 14737632);
                gui.drawResource(hovered ? focused : normal, x + 3, y + 3);
            }
        });
    }
}
