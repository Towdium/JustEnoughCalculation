package me.towdium.jecalculation.client.widget.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.Widget;
import me.towdium.jecalculation.core.entry.Entry;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WEntry extends Widget.Advanced {
    public int xPos, yPos, xSize, ySize;
    public Entry entry;

    public WEntry(int xPos, int yPos, int xSize, int ySize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.entry = Entry.EMPTY;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_SLOT, xPos + gui.getGuiLeft(), yPos + gui.getGuiTop(),
                xSize, ySize, 3, 3, 3, 3);
        gui.drawItemStack(gl(gui) + xPos + xSize / 2, gt(gui) + yPos + ySize / 2, entry.getRepresentation(), true);
        if (mouseIn(gui, xMouse, yMouse)) gui.drawRectangle(gui.getGuiLeft() + xPos + 1, gui.getGuiTop() + yPos + 1,
                xSize - 2, ySize - 2, 0x80FFFFFF);
        // TODO draw amount
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        if (mouseIn(gui, xMouse, yMouse)) {  // TODO different modes
            if (gui.hand != Entry.EMPTY && button == 0) {
                entry = gui.hand;
                gui.hand = Entry.EMPTY;
                return true;
            }
        }
        return false;
    }

    public boolean mouseIn(JecGui gui, int x, int y) {
        int xx = x - gui.getGuiLeft() - xPos;
        int yy = y - gui.getGuiTop() - yPos;
        return xx >= 0 && xx < xSize && yy >= 0 && yy < ySize;
    }
}
