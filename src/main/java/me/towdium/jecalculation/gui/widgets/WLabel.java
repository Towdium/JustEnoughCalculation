package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.Utilities.Timer;
import mezz.jei.api.recipe.IFocus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_RED;
import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_WHITE;
import static me.towdium.jecalculation.gui.JecaGui.Font.HALF;
import static me.towdium.jecalculation.gui.JecaGui.Font.PLAIN;
import static me.towdium.jecalculation.gui.Resource.*;
import static me.towdium.jecalculation.jei.JecaPlugin.runtime;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WLabel implements IWidget {
    public int xPos, yPos, xSize, ySize;
    ILabel label;
    public Mode mode;
    public ListenerValue<? super WLabel, ILabel> listener;
    protected Timer timer = new Timer();

    public WLabel(int xPos, int yPos, int xSize, int ySize, Mode mode) {
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

    public WLabel setLabel(ILabel label) {
        this.label = label;
        return this;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(WGT_SLOT, xPos, yPos, xSize, ySize, 3, 3, 3, 3);
        label.drawLabel(gui, xPos + xSize / 2, yPos + ySize / 2, true);
        if (mode == Mode.RESULT || mode == Mode.EDITOR) {
            String s = label.getAmountString(mode == Mode.RESULT);
            gui.drawText(xPos + xSize / 2.0f + 8 - HALF.getTextWidth(s),
                    yPos + ySize / 2.0f + 8.5f - HALF.getTextHeight(), HALF, s);
        }
        if (mode == Mode.EDITOR || mode == Mode.SELECTOR) {
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
            label.getToolTip(tooltip, mode == Mode.EDITOR || mode == Mode.RESULT);
        }
        return false;
    }

    @Override
    public boolean onScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        if (mouseIn(xMouse, yMouse)) {
            if (mode == Mode.EDITOR && label != ILabel.EMPTY) {
                for (int i = 0; i < Math.abs(diff); i++)
                    label = diff > 0 ? label.increaseAmount() : label.decreaseAmount();
                notifyLsnr();
            }
            return true;
        } else return false;
    }


    @Nullable
    @Override
    public ILabel getLabelUnderMouse(int xMouse, int yMouse) {
        return mouseIn(xMouse, yMouse) ? label : null;
    }

    @Override
    public boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!mouseIn(xMouse, yMouse) || button == 1) return false;
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
                if (rep != null) runtime.getRecipesGui().show(runtime.getRecipeManager()
                        .createFocus(IFocus.Mode.OUTPUT, rep));
                return rep != null;
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
                throw new RuntimeException("Internal error");
        }
    }

    public WLabel setListener(ListenerValue<? super WLabel, ILabel> listener) {
        this.listener = listener;
        return this;
    }

    public boolean mouseIn(int x, int y) {
        int xx = x - xPos;
        int yy = y - yPos;
        return xx >= 0 && xx < xSize && yy >= 0 && yy < ySize;
    }

    void notifyLsnr() {
        if (listener != null) listener.invoke(this, label);
    }

    public enum Mode {
        EDITOR,  // Slots in editor gui. Can use to edit amount. Exact amount displayed.
        RESULT,  // Slots to display calculate result. Rounded amount displayed.
        PICKER,  // Slots that can pick items from. No amount displayed.
        SELECTOR  // Slots to put labels into. No amount displayed.
    }

    class WAmount extends WOverlay {
        WLabel temp = new WLabel(xPos, yPos, xSize, ySize, Mode.PICKER).setListener((i, v) -> update());
        WButton number = new WButtonText(xPos + xSize + 60, yPos, 20, 20, "#", "general.to_percent")
                .setListener(i -> {
                    temp.label.setPercent(true);
                    update();
                });
        WTextField text = new WTextField(xPos + xSize + 10, yPos + ySize / 2 - WTextField.HEIGHT / 2, 50);
        WButton percent = new WButtonText(xPos + xSize + 60, yPos, 20, 20, "%", "general.to_percent")
                .setListener(i -> {
                    temp.label.setPercent(false);
                    update();
                });
        WButton pick = new WButtonIcon(xPos + xSize + 83, yPos, 20, 20, BTN_PICK, "label.pick").setListener(i -> {
            JecaGui.getCurrent().hand = temp.label;
            setLabel(ILabel.EMPTY);
            notifyLsnr();
            JecaGui.getCurrent().root.remove(this);
        });
        WButton yes = new WButtonIcon(xPos + xSize + 102, yPos, 20, 20, BTN_YES, "label.confirm").setListener(i -> {
            setLabel(temp.label);
            notifyLsnr();
            JecaGui.getCurrent().root.remove(this);
        });
        WButton no = new WButtonIcon(xPos + xSize + 121, yPos, 20, 20, BTN_NO, "label.delete").setListener(i -> {
            setLabel(ILabel.EMPTY);
            notifyLsnr();
            JecaGui.getCurrent().root.remove(this);
        });

        public WAmount() {
            temp.setLabel(label.copy());
            add(new WPanel(xPos - 5, yPos - 5, xSize + 152, ySize + 10));
            add(new WText(xPos + xSize + 3, yPos + 5, PLAIN, "x"));
            add(temp, text, pick, yes, no);
            text.setListener(i -> {
                boolean acceptable;
                long amount;
                try {
                    amount = Long.parseLong(text.getText());
                    acceptable = amount > 0;
                    if (!acceptable) amount = 1;
                } catch (NumberFormatException e) {
                    acceptable = text.getText().isEmpty();
                    amount = 1;
                }
                text.setColor(acceptable ? COLOR_TEXT_WHITE : COLOR_TEXT_RED);
                yes.setDisabled(!acceptable);
                temp.label = temp.label.setAmount(amount);
            });
            update();
        }

        private void update() {
            number.setDisabled(!temp.label.acceptPercent());
            if (temp.label.isPercent()) {
                remove(number);
                add(percent);
            } else {
                remove(percent);
                add(number);
            }
            text.setText(Long.toString(temp.label.getAmount()));
        }
    }
}
