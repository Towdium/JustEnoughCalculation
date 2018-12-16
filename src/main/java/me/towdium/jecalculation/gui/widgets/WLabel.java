package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.IllegalPositionException;
import me.towdium.jecalculation.utils.Utilities.Timer;
import mezz.jei.api.recipe.IFocus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.IntStream;

import static me.towdium.jecalculation.gui.JecaGui.Font.HALF;
import static me.towdium.jecalculation.jei.JecaPlugin.runtime;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WLabel implements IWidget {
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
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_SLOT, xPos, yPos, xSize, ySize, 3, 3, 3, 3);
        label.drawLabel(gui, xPos + xSize / 2, yPos + ySize / 2, true);
        if (mode == enumMode.RESULT || mode == enumMode.EDITOR) {
            String s = label.getAmountString(mode == enumMode.RESULT);
            gui.drawText(xPos + xSize / 2.0f + 8 - HALF.getTextWidth(s),
                    yPos + ySize / 2.0f + 8.5f - HALF.getTextHeight(), HALF, s);
        }
        if (mode == enumMode.EDITOR || mode == enumMode.SELECTOR) {
            timer.setState(gui.hand != ILabel.EMPTY);
            int color = 0xFFFFFF + (int) ((-Math.cos(timer.getTime() * Math.PI / 1500) + 1) * 0x40) * 0x1000000;
            gui.drawRectangle(xPos + 1, yPos + 1, xSize - 2, ySize - 2, color);
        }
        if (mouseIn(xMouse, yMouse)) gui.drawRectangle(xPos + 1, yPos + 1, xSize - 2, ySize - 2, 0x80FFFFFF);
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        if (!mouseIn(xMouse, yMouse)) return false;
        if (label != ILabel.EMPTY) {
            tooltip.add(label.getDisplayName());
            label.getToolTip(tooltip, mode == enumMode.EDITOR || mode == enumMode.RESULT);
        }
        return false;
    }

    @Override
    public boolean onScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        if (mouseIn(xMouse, yMouse) && mode == enumMode.EDITOR && label != ILabel.EMPTY) {
            IntStream.range(0, Math.abs(diff)).forEach(i ->
                    label = diff > 0 ? label.increaseAmount() : label.decreaseAmount());
            notifyLsnr();
            return true;
        } else return false;
    }

    @Override
    public boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!mouseIn(xMouse, yMouse)) return false;
        switch (mode) {
            case EDITOR:
                if (gui.hand != label.EMPTY) {
                    label = gui.hand;
                    gui.hand = label.EMPTY;
                    notifyLsnr();
                    return true;
                } else if (label != label.EMPTY) {
                    gui.root.add(new WAmount());
                    return true;
                } else return false;
            case RESULT:
                Object rep = label.getRepresentation();
                if (rep != null) {
                    runtime.getRecipesGui().show(runtime.getRecipeRegistry().createFocus(IFocus.Mode.OUTPUT, rep));
                    return true;
                } else return false;
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
        EDITOR,  // Slots in editor gui. Can use to edit amount. Exact amount displayed.
        RESULT,  // Slots to display calculate/getRecipes result. Rounded amount displayed.
        PICKER,  // Slots that can pick items from. No amount displayed.
        SELECTOR  // Slots to put labels into. No amount displayed.
    }

    class WAmount extends WContainer {
        WLabel wl = new WLabel(xPos, yPos, xSize, ySize, enumMode.SELECTOR).setLsnrUpdate(this::update);
        WTextField wtf = new WTextField(xPos + xSize + 10, yPos + ySize / 2 - WTextField.HEIGHT / 2, 50);
        WButton bPercent = new WButtonText(xPos + xSize + 60, yPos, 20, 20, "general.to_percent", "%")
                .setLsnrLeft(() -> setPercent(false));
        WButton bAmount = new WButtonText(xPos + xSize + 60, yPos, 20, 20, "general.to_percent", "#")
                .setLsnrLeft(() -> setPercent(true));
        WButton bYes = new WButtonIcon(xPos + xSize + 83, yPos, 20, 20, Resource.BTN_YES).setLsnrLeft(() -> {
            setLabel(wl.getLabel().setAmount(
                    wtf.getText().isEmpty() ? 0 : Integer.parseInt(wtf.getText())));
            JecaGui.getCurrent().root.remove(this);
        });
        WButton bNo = new WButtonIcon(xPos + xSize + 102, yPos, 20, 20, Resource.BTN_NO).setLsnrLeft(() -> {
            setLabel(ILabel.EMPTY);
            JecaGui.getCurrent().root.remove(this);
        });

        public WAmount() {
            wl.setLabel(label);
            add(new WPanel(xPos - 5, yPos - 5, xSize + 133, ySize + 10));
            add(new WText(xPos + xSize + 3, yPos + 5, JecaGui.Font.PLAIN, "x"));
            addAll(wl, wtf, bYes, bNo);
            wtf.setLsnrText(i -> {
                try {
                    Integer.parseInt(wtf.getText());
                    wtf.setColor(JecaGui.COLOR_TEXT_WHITE);
                    bYes.setDisabled(false);
                } catch (NumberFormatException e) {
                    boolean acceptable = wtf.getText().isEmpty();
                    wtf.setColor(acceptable ? JecaGui.COLOR_TEXT_WHITE : JecaGui.COLOR_TEXT_RED);
                    bYes.setDisabled(!acceptable);
                }
            });
            update();
        }

        private void update() {
            label = wl.label;
            bAmount.setDisabled(!wl.label.acceptPercent());
            setPercent(label.isPercent());
        }

        private void setPercent(boolean b) {
            if (b) {
                remove(bAmount);
                add(bPercent);
            } else {
                remove(bPercent);
                add(bAmount);
            }
            if (label.acceptPercent()) label.setPercent(b);
            wtf.setText(Long.toString(label.getAmount()));
        }

        @Override
        public boolean onKey(JecaGui gui, char ch, int code) {
            if (super.onKey(gui, ch, code)) return true;
            if (code == Keyboard.KEY_ESCAPE) {
                gui.root.remove(this);
                return true;
            } else return false;
        }
    }

    @Nullable
    @Override
    public ILabel getLabelUnderMouse(int xMouse, int yMouse) {
        return mouseIn(xMouse, yMouse) ? label : null;
    }
}
