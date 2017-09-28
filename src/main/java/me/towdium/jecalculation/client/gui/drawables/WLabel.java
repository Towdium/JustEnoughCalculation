package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IWidget;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.IllegalPositionException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WLabel implements IWidget {
    static JecGui.Font font;

    static {
        font = JecGui.Font.DEFAULT_HALF.copy();
        font.align = JecGui.Font.enumAlign.RIGHT;
    }

    public int xPos, yPos, xSize, ySize;
    public ILabel label;
    public enumMode mode;

    public WLabel(int xPos, int yPos, int xSize, int ySize, enumMode mode) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.label = label.EMPTY;
        this.mode = mode;
    }

    public ILabel getLabel() {
        return label;
    }

    public void setLabel(ILabel label) {
        this.label = label;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_SLOT, xPos, yPos, xSize, ySize, 3, 3, 3, 3);
        label.drawLabel(gui, xPos + xSize / 2, yPos + ySize / 2, true);
        if (mode == enumMode.RESULT || mode == enumMode.EDITOR)
            gui.drawText(xPos + xSize / 2 + 7.5f, yPos + ySize / 2 + 7 -
                    (int) (font.size * gui.getFontRenderer().FONT_HEIGHT), font, label.getAmountString());
        if (mouseIn(xMouse, yMouse)) {
            gui.drawRectangle(xPos + 1, yPos + 1, xSize - 2, ySize - 2, 0x80FFFFFF);
            if (label != ILabel.EMPTY) {
                ArrayList<String> buf = new ArrayList<>();
                buf.add(label.getDisplayName());
                gui.drawTooltip(xMouse, yMouse,
                        label.getToolTip(buf, mode == enumMode.EDITOR || mode == enumMode.RESULT));
            }
        }
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        if (mouseIn(xMouse, yMouse)) {
            switch (mode) {
                case EDITOR:
                    if (gui.hand != label.EMPTY) {
                        label = gui.hand;
                        gui.hand = label.EMPTY;
                        return true;
                    } else if (label != label.EMPTY) {
                        if (button == 0) {
                            if (JecGui.isShiftDown()) label = label.increaseAmountLarge();
                            else label = label.increaseAmount();
                            return true;
                        } else if (button == 1) {
                            if (JecGui.isShiftDown()) label = label.decreaseAmountLarge();
                            else label = label.decreaseAmount();
                            return true;
                        }
                    } else return false;
                case RESULT:
                    return false;
                case PICKER:
                    if (label != label.EMPTY) {
                        gui.hand = label.copy();
                        return true;
                    } else return false;
                case SELECTOR:
                    label = gui.hand;
                    gui.hand = label.EMPTY;
                    return true;
                default:
                    throw new IllegalPositionException();
            }
        } else return false;
    }

    public boolean mouseIn(int x, int y) {
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
