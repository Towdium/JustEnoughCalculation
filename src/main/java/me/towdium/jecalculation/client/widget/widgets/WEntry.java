package me.towdium.jecalculation.client.widget.widgets;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.Widget;
import me.towdium.jecalculation.core.entry.Entry;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
public class WEntry extends Widget.Advanced {
    public int xPos, yPos, xSize, ySize;
    public Entry entry;

    public WEntry(int xPos, int yPos, int xSize, int ySize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WIDGET_SLOT, xPos + gui.getGuiLeft(), yPos + gui.getGuiTop(),
                xSize, ySize, 3, 3, 3, 3);
        if (mouseIn(gui, xMouse, yMouse)) drawRect(gui.getGuiLeft() + xPos + 1, gui.getGuiTop() + yPos + 1,
                gui.getGuiLeft() + xPos + xSize - 1, gui.getGuiTop() + yPos + ySize - 1, 0x80FFFFFF);
        // TODO draw itemStack
    }

    public boolean mouseIn(JecGui gui, int x, int y) {
        int xx = x - gui.getGuiLeft() - xPos;
        int yy = y - gui.getGuiTop() - yPos;
        return xx >= 0 && xx < xSize && yy >= 0 && yy < ySize;
    }
}
