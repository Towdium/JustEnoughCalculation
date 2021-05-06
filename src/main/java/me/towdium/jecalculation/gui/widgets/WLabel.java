package me.towdium.jecalculation.gui.widgets;

import codechicken.lib.gui.GuiDraw;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.Utilities.Timer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

import static me.towdium.jecalculation.gui.JecaGui.Font.HALF;
import static me.towdium.jecalculation.gui.Resource.WGT_SLOT;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WLabel implements IWidget {
    public int xPos, yPos, xSize, ySize;
    ILabel label = ILabel.EMPTY;
    public boolean multiple, accept;
    public ListenerValue<? super WLabel, ILabel> update;
    public ListenerAction<? super WLabel> click;
    Function<ILabel, String> formatter = i -> i.getAmountString(false);
    protected Timer timer = new Timer();

    public WLabel(int xPos, int yPos, int xSize, int ySize, boolean multiple, boolean accept) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.multiple = multiple;
        this.accept = accept;
    }

    public ILabel getLabel() {
        return label;
    }

    public WLabel setLabel(ILabel label) {
        return setLabel(label, false);
    }

    public WLabel setLabel(ILabel label, boolean notify) {
        this.label = label;
        if (notify)
            notifyUpdate();
        return this;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(WGT_SLOT, xPos, yPos, xSize, ySize, 3, 3, 3, 3);
        label.drawLabel(gui, xPos + xSize / 2, yPos + ySize / 2, true);
        if (multiple) {
            String s = formatter.apply(label);
            gui.drawText(xPos + xSize / 2.0f + 8 - HALF.getTextWidth(s),
                         yPos + ySize / 2.0f + 8.5f - HALF.getTextHeight(), HALF, s);
        }
        if (accept) {
            timer.setState(gui.hand != ILabel.EMPTY);
            int color = 0xFFFFFF + (int) ((-Math.cos(timer.getTime() * Math.PI / 1500) + 1) * 0x40) * 0x1000000;
            GuiDraw.drawRect(xPos + 1, yPos + 1, xSize - 2, ySize - 2, color);
        }
        if (mouseIn(xMouse, yMouse))
            GuiDraw.drawRect(xPos + 1, yPos + 1, xSize - 2, ySize - 2, 0x80FFFFFF);
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        if (!mouseIn(xMouse, yMouse))
            return false;
        if (label != ILabel.EMPTY) {
            tooltip.add(label.getDisplayName());
            label.getToolTip(tooltip, multiple);
        }
        return false;
    }

    // TODO

    //    @Override
    //    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
    //        if (mouseIn(xMouse, yMouse)) {
    //            if (mode == Mode.EDITOR && label != ILabel.EMPTY) {
    //                for (int i = 0; i < Math.abs(diff); i++)
    //                    label = diff > 0 ? label.increaseAmount() : label.decreaseAmount();
    //                notifyLsnr();
    //            }
    //            return true;
    //        } else
    //            return false;
    //    }

    @Nullable
    @Override
    public WLabel getLabelUnderMouse(int xMouse, int yMouse) {
        return mouseIn(xMouse, yMouse) ? this : null;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!mouseIn(xMouse, yMouse) || button == 1)
            return false;
        if (accept) {
            if (gui.hand == ILabel.EMPTY && click != null)
                notifyClick();
            else {
                label = gui.hand;
                gui.hand = label.EMPTY;
                notifyUpdate();
            }
        } else
            notifyClick();
        return true;
        //        if (!mouseIn(xMouse, yMouse) || (button == 1 && mode != Mode.RESULT))
        //            return false;
        //        switch (mode) {
        //            case EDITOR:
        //                if (gui.hand != label.EMPTY) {
        //                    label = gui.hand;
        //                    gui.hand = label.EMPTY;
        //                    notifyLsnr();
        //                    return true;
        //                } else if (label != label.EMPTY) {
        //                    gui.root.add(new WAmount());
        //                    return true;
        //                } else
        //                    return false;
        //            case RESULT:
        //                // open NEI recipe gui
        //                Object item = label.getRepresentation();
        //                if ((item instanceof ItemStack || item instanceof FluidStack)) {
        //                    String id = item instanceof ItemStack ? "item" : "liquid";
        //                    if (button == 0) {
        //                        GuiCraftingRecipe.openRecipeGui(id, item);
        //                        return true;
        //                    } else if (button == 1) {
        //                        GuiUsageRecipe.openRecipeGui(id, item);
        //                        return true;
        //                    }
        //                } else if (item != null) {
        //                    JustEnoughCalculation.logger.warn("unknown label representation " + item);
        //                }
        //                return false;
        //            case PICKER:
        //                if (label != label.EMPTY) {
        //                    notifyLsnr();
        //                    return true;
        //                } else
        //                    return false;
        //            case SELECTOR:
        //                label = gui.hand;
        //                gui.hand = label.EMPTY;
        //                notifyLsnr();
        //                return true;
        //            default:
        //                throw new IllegalPositionException();
        //        }
    }

    public WLabel setLsnrUpdate(ListenerValue<? super WLabel, ILabel> listener) {
        update = listener;
        return this;
    }

    public WLabel setLsnrClick(ListenerAction<? super WLabel> listener) {
        click = listener;
        return this;
    }

    public WLabel setFormatter(Function<ILabel, String> f) {
        formatter = f;
        return this;
    }

    public boolean mouseIn(int x, int y) {
        int xx = x - xPos;
        int yy = y - yPos;
        return xx >= 0 && xx < xSize && yy >= 0 && yy < ySize;
    }

    private void notifyClick() {
        if (click != null)
            click.invoke(this);
    }

    private void notifyUpdate() {
        if (update != null)
            update.invoke(this, label);
    }


    //    class WAmount extends WOverlay {
    //        WLabel temp = new WLabel(xPos, yPos, xSize, ySize, Mode.PICKER).setListener((i, v) -> update());
    //        WButton number = new WButtonText(xPos + xSize + 60, yPos, 20, 20, "#", "general.to_percent").setListener(i -> {
    //            temp.label.setPercent(true);
    //            update();
    //        });
    //        WTextField text = new WTextField(xPos + xSize + 10, yPos + ySize / 2 - WTextField.HEIGHT / 2, 50);
    //        WButton percent = new WButtonText(xPos + xSize + 60, yPos, 20, 20, "%", "general.to_percent").setListener(i -> {
    //            temp.label.setPercent(false);
    //            update();
    //        });
    //        WButton pick = new WButtonIcon(xPos + xSize + 83, yPos, 20, 20, BTN_PICK, "label.pick").setListener(i -> {
    //            JecaGui.getCurrent().hand = temp.label;
    //            setLabel(ILabel.EMPTY);
    //            notifyLsnr();
    //            JecaGui.getCurrent().root.remove(this);
    //        });
    //        WButton yes = new WButtonIcon(xPos + xSize + 102, yPos, 20, 20, BTN_YES, "label.confirm").setListener(i -> {
    //            setLabel(temp.label);
    //            notifyLsnr();
    //            JecaGui.getCurrent().root.remove(this);
    //        });
    //        WButton no = new WButtonIcon(xPos + xSize + 121, yPos, 20, 20, BTN_NO, "label.delete").setListener(i -> {
    //            setLabel(ILabel.EMPTY);
    //            notifyLsnr();
    //            JecaGui.getCurrent().root.remove(this);
    //        });
    //
    //        public WAmount() {
    //            temp.setLabel(label.copy());
    //            add(new WPanel(xPos - 5, yPos - 5, xSize + 152, ySize + 10));
    //            add(new WText(xPos + xSize + 3, yPos + 5, PLAIN, "x"));
    //            add(temp, text, pick, yes, no);
    //            text.setListener(i -> {
    //                boolean acceptable;
    //                long amount;
    //                try {
    //                    amount = Long.parseLong(text.getText());
    //                    acceptable = amount > 0;
    //                    if (!acceptable)
    //                        amount = 1;
    //                } catch (NumberFormatException e) {
    //                    acceptable = text.getText().isEmpty();
    //                    amount = 1;
    //                }
    //                text.setColor(acceptable ? COLOR_TEXT_WHITE : COLOR_TEXT_RED);
    //                yes.setDisabled(!acceptable);
    //                temp.label = temp.label.setAmount(amount);
    //            });
    //            update();
    //        }
    //
    //        private void update() {
    //            number.setDisabled(!temp.label.acceptPercent());
    //            if (temp.label.isPercent()) {
    //                remove(number);
    //                add(percent);
    //            } else {
    //                remove(percent);
    //                add(number);
    //            }
    //            text.setText(Long.toString(temp.label.getAmount()));
    //        }
    //    }
}
