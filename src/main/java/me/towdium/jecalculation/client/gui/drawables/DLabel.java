package me.towdium.jecalculation.client.gui.drawables;

import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.IllegalPositionException;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
public class DLabel implements IDrawable {
    static JecGui.Font font;

    static {
        font = JecGui.Font.DEFAULT_HALF.copy();
        font.align = JecGui.Font.enumAlign.RIGHT;
    }

    public int xPos, yPos, xSize, ySize;
    public ILabel ILabel;
    public enumMode mode;

    public DLabel(int xPos, int yPos, int xSize, int ySize, enumMode mode) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.ILabel = ILabel.EMPTY;
        this.mode = mode;
    }

    public ILabel getILabel() {
        return ILabel;
    }

    public void setILabel(ILabel ILabel) {
        this.ILabel = ILabel;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_SLOT, xPos, yPos, xSize, ySize, 3, 3, 3, 3);
        ILabel.drawEntry(gui, xPos + xSize / 2, yPos + ySize / 2, true);
        if (mode == enumMode.RESULT || mode == enumMode.EDITOR)
            gui.drawText(xPos + xSize / 2 + 7.5f,
                         yPos + ySize / 2 + 7 - (int) (font.size * gui.getFontRenderer().FONT_HEIGHT), font,
                         ILabel.getAmountString());
        if (mouseIn(gui, xMouse, yMouse))
            gui.drawRectangle(xPos + 1, yPos + 1, xSize - 2, ySize - 2, 0x80FFFFFF);
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        if (mouseIn(gui, xMouse, yMouse)) {
            switch (mode) {
                case EDITOR:
                    if (gui.hand != ILabel.EMPTY) {
                        ILabel = gui.hand;
                        gui.hand = ILabel.EMPTY;
                        return true;
                    } else if (ILabel != ILabel.EMPTY) {
                        if (button == 0) {
                            if (JecGui.isShiftDown())
                                ILabel = ILabel.increaseAmountLarge();
                            else
                                ILabel = ILabel.increaseAmount();
                            return true;
                        } else if (button == 1) {
                            if (JecGui.isShiftDown())
                                ILabel = ILabel.decreaseAmountLarge();
                            else
                                ILabel = ILabel.decreaseAmount();
                            return true;
                        }
                    } else
                        return false;
                case RESULT:
                    return false;
                case PICKER:
                    if (ILabel != ILabel.EMPTY) {
                        gui.hand = ILabel.copy();
                        return true;
                    } else
                        return false;
                case SELECTOR:
                    ILabel = gui.hand;
                    gui.hand = ILabel.EMPTY;
                    return true;
                default:
                    throw new IllegalPositionException();
            }
        } else
            return false;
    }

    public boolean mouseIn(JecGui gui, int x, int y) {
        int xx = x - xPos;
        int yy = y - yPos;
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
