package me.towdium.jecalculation.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.IWidget;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.IllegalPositionException;
import me.towdium.jecalculation.utils.Utilities.Timer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WLabel implements IWidget {
    static JecaGui.Font font;

    static {
        font = JecaGui.Font.DEFAULT_HALF.copy();
        font.align = JecaGui.Font.enumAlign.RIGHT;
    }

    public int xPos, yPos, xSize, ySize;
    public ILabel label;
    public enumMode mode;
    public Runnable lsnrUpdate;
    protected Timer timer = new Timer();

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
        //if (mode == enumMode.EDITOR || mode == enumMode.SELECTOR)
        //   notifyLsnr();
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_SLOT, xPos, yPos, xSize, ySize, 3, 3, 3, 3);
        label.drawLabel(gui, xPos + xSize / 2, yPos + ySize / 2, true);
        if (mode == enumMode.RESULT || mode == enumMode.EDITOR)
            gui.drawText(xPos + xSize / 2.0f + 7.5f, yPos + ySize / 2 + 7 -
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
        if (mode == enumMode.EDITOR || mode == enumMode.SELECTOR) {
            timer.setState(gui.hand != ILabel.EMPTY);
            int color = 0xFFFFFF + (int) ((-Math.cos(timer.getTime() * Math.PI / 1500) + 1) * 0x40) * 0x1000000;
            gui.drawRectangle(xPos + 1, yPos + 1, xSize - 2, ySize - 2, color);
        }
    }

    @Override
    public boolean onScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        if (mouseIn(xMouse, yMouse) && mode == enumMode.EDITOR && label != ILabel.EMPTY) {
            IntStream.range(0, Math.abs(diff)).forEach(i -> {
                if (JecaGui.isShiftDown()) label = diff > 0 ? label.increaseAmountLarge() : label.decreaseAmountLarge();
                else label = diff > 0 ? label.increaseAmount() : label.decreaseAmount();
            });
            notifyLsnr();
            return true;
        } else return false;
    }

    @Override
    public boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (mouseIn(xMouse, yMouse)) {
            switch (mode) {
                case EDITOR:
                    if (gui.hand != label.EMPTY) {
                        label = gui.hand;
                        gui.hand = label.EMPTY;
                        notifyLsnr();
                        return true;
                    } else if (label != label.EMPTY) {
                        if (button == 0) {
                            if (JecaGui.isShiftDown()) label = label.increaseAmountLarge();
                            else label = label.increaseAmount();
                            notifyLsnr();
                            return true;
                        } else if (button == 1) {
                            if (JecaGui.isShiftDown()) label = label.decreaseAmountLarge();
                            else label = label.decreaseAmount();
                            notifyLsnr();
                            return true;
                        }
                    } else return false;
                case RESULT:
                    return false;
                case PICKER:
                    if (label != label.EMPTY) {
                        notifyLsnr();
                        return true;
                    } else return false;
                case SELECTOR:
                    label = gui.hand;
                    gui.hand = label.EMPTY;
                    notifyLsnr();
                    return true;
                default:
                    throw new IllegalPositionException();
            }
        } else return false;
    }

    public WLabel setLsnrUpdate(Runnable lsnr) {
        lsnrUpdate = lsnr;
        return this;
    }

    public boolean mouseIn(int x, int y) {
        int xx = x - xPos;
        int yy = y - yPos;
        return xx >= 0 && xx < xSize && yy >= 0 && yy < ySize;
    }

    void notifyLsnr() {
        if (lsnrUpdate != null) lsnrUpdate.run();
    }

    public enum enumMode {
        /**
         * Slots in editor gui. Can use to edit amount. Exact amount displayed.
         */
        EDITOR,
        /**
         * Slots to display calculate/getRecipes result. Rounded amount displayed.
         */
        RESULT,
        /**
         * Slots that can pick items from. No amount displayed.
         */
        PICKER,
        /**
         * Slots to put labels into. No amount displayed.
         */
        SELECTOR
    }
}
