package me.towdium.jecalculation.client.gui.widget.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.resource.Resource;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WButtonIcon extends WButton {
    protected int xPos, yPos, xSize, ySize;
    protected Resource normal, focused;

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize, Resource normal, Resource focused) {
        this(xPos, yPos, xSize, ySize, normal, focused, null);
    }

    public WButtonIcon(int xPos, int yPos, int xSize, int ySize,
                       Resource normal, Resource focused, @Nullable String tooltip) {
        super(xPos, yPos, xSize, ySize, "", tooltip);
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.normal = normal;
        this.focused = focused;
    }

    @Override
    public void onGuiInit(JecGui gui) {
        button = new GuiButtonExt(0, xPos + gui.getGuiLeft(), yPos + gui.getGuiTop(), xSize, ySize, "") {
            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
                this.hovered = mouseX > x + 1 && mouseY > y + 1 && mouseX <= x + width - 1 && mouseY <= y + height - 1;
                gui.drawResourceContinuous(hovered ? Resource.WGT_BUTTON_F : Resource.WGT_BUTTON_N, x, y,
                        width, height, 3, 3, 3, 3);
                Resource r = hovered ? focused : normal;
                gui.drawResource(r, x + (width - r.getXSize()) / 2, y + (height - r.getYSize()) / 2);
            }

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                return false;
            }
        };
        gui.buttonList.add(button);
    }

    public WButtonIcon setListenerLeft(Runnable r) {
        return ((WButtonIcon) super.setListenerLeft(r));
    }

    public WButtonIcon setListenerRight(Runnable r) {
        return ((WButtonIcon) super.setListenerRight(r));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public WButtonIcon setEnabled(boolean e) {
        if (!e) throw new RuntimeException("Unsupported");
        else return this;
    }
}
