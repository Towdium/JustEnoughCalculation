package me.towdium.jecalculation.client.widget.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.Widget;
import me.towdium.jecalculation.core.entry.Entry;
import me.towdium.jecalculation.utils.IllegalPositionException;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WEntry extends Widget.Advanced {
    static JecGui.Font font;

    static {
        font = JecGui.Font.DEFAULT_HALF.copy();
        font.align = JecGui.Font.enumAlign.RIGHT;
    }

    public int xPos, yPos, xSize, ySize;
    public Entry entry;
    public enumMode mode;

    public WEntry(int xPos, int yPos, int xSize, int ySize, enumMode mode) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.entry = Entry.EMPTY;
        this.mode = mode;
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
        if (mode == enumMode.RESULT || mode == enumMode.EDITOR)
            gui.drawText(gl(gui) + xPos + xSize / 2 + 7.5f, gt(gui) + yPos + ySize / 2 + 7 -
                    (int) (font.size * gui.getFontRenderer().FONT_HEIGHT), font, entry.getAmountString());
        if (mouseIn(gui, xMouse, yMouse)) gui.drawRectangle(gui.getGuiLeft() + xPos + 1, gui.getGuiTop() + yPos + 1,
                xSize - 2, ySize - 2, 0x80FFFFFF);
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        if (mouseIn(gui, xMouse, yMouse)) {
            switch (mode) {
                case EDITOR:
                    if (gui.hand != Entry.EMPTY) {
                        entry = gui.hand;
                        gui.hand = Entry.EMPTY;
                        return true;
                    } else if (entry != Entry.EMPTY) {
                        if (button == 0) {
                            if (JecGui.isShiftDown()) entry = entry.increaseAmountLarge();
                            else entry = entry.increaseAmount();
                            return true;
                        } else if (button == 1) {
                            if (JecGui.isShiftDown()) entry = entry.decreaseAmountLarge();
                            else entry = entry.decreaseAmount();
                            return true;
                        }
                    } else return false;
                case RESULT:
                    return false;
                case PICKER:
                    if (entry != Entry.EMPTY) {
                        gui.hand = entry.copy();
                        return true;
                    } else return false;
                case SELECTOR:
                    entry = gui.hand;
                    gui.hand = Entry.EMPTY;
                    return true;
                default:
                    throw new IllegalPositionException();
            }
        } else return false;
    }

    public boolean mouseIn(JecGui gui, int x, int y) {
        int xx = x - gui.getGuiLeft() - xPos;
        int yy = y - gui.getGuiTop() - yPos;
        return xx >= 0 && xx < xSize && yy >= 0 && yy < ySize;
    }

    public enum enumMode {
        /**
         * Slots in editor gui. Can use to edit amount. Exact amount displayed.
         */
        EDITOR,
        /**
         * Slots to display calculate/search result. Rounded amount displayed.
         */
        RESULT,
        /**
         * Slots that can pick items from. No amount displayed.
         */
        PICKER,
        /**
         * Slots to select items, eg. in calculate and search gui. No amount displayed.
         */
        SELECTOR
    }
}
