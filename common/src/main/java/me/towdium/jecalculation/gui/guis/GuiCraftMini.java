package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.CostList.Calculator;
import me.towdium.jecalculation.data.structure.RecordCraft;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.Utilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static me.towdium.jecalculation.data.structure.RecordCraft.Mode.*;
import static me.towdium.jecalculation.utils.Utilities.getPlayer;

/**
 * Author: ekgame
 * Date:   9/04/21.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Environment(EnvType.CLIENT)
public class GuiCraftMini extends WContainer {
    public final int WINDOW_WIDTH = 108;
    public final int WINDOW_HEIGHT = 113;

    protected int slot = 0;
    protected ItemStack itemStack;
    protected Calculator calculator = null;
    public RecordCraft record;
    protected ListenerAction<? super GuiCraftMini> windowFocusListener = null;
    protected ListenerAction<? super GuiCraftMini> windowCloseListener = null;

    WDrag drag = new WDrag(4, 4, 95, 5);

    WButton close = new WCross(99, 4)
            .setListener((widget) -> {
                record.overlayOpen = false;
                Controller.setRCraft(record, itemStack, slot);

                if (windowCloseListener != null) {
                    windowCloseListener.invoke(this);
                }
            });

    WLabel label = new WLabel(5, 10, 20, 20, false);

    WTextField amount = new WTextField(34, 10, 69)
            .setListener(i -> {
                record.amount = i.getText();
                Controller.setRCraft(record, itemStack, slot);
                refreshCalculator();
            });

    WButton invE = new WButtonIcon(83, 32, 20, 20, Resource.BTN_INV_E, "craft.inventory_enabled");
    WButton invD = new WButtonIcon(83, 32, 20, 20, Resource.BTN_INV_D, "craft.inventory_disabled");

    WButton input = new WButtonIcon(5, 32, 20, 20, Resource.BTN_IN, "common.input")
            .setListener(i -> setMode(INPUT));

    WButton output = new WButtonIcon(24, 32, 20, 20, Resource.BTN_OUT, "craft.output")
            .setListener(i -> setMode(OUTPUT));

    WButton catalyst = new WButtonIcon(42, 32, 20, 20, Resource.BTN_CAT, "common.catalyst")
            .setListener(i -> setMode(CATALYST));

    WButton steps = new WButtonIcon(61, 32, 20, 20, Resource.BTN_LIST, "craft.step")
            .setListener(i -> setMode(STEPS));

    WLabelScroll result = new WLabelScroll(5, 54, 5, 3, false, 0, 8)
            .setLsnrClick((i, v) -> Utilities.showRecipe(i.get(v).getLabel()))
            .setFmtAmount(i -> i.getAmountString(true))
            .setFmtTooltip((i, j) -> i.getToolTip(j, true));

    public GuiCraftMini(@Nullable ItemStack is, int slot) {
        itemStack = is;
        record = Controller.getRCraft(is);
        this.slot = slot;
        this.offsetX = record.overlayPositionX;
        this.offsetY = record.overlayPositionY;
        amount.setText(record.amount);

        add(new WPanel(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT));
        add(new WText(27, 16, JecaGui.FontType.RAW, "x"));
        add(drag, close, label, input, output, catalyst, steps, result, amount, record.inventory ? invE : invD);

        drag.setDragStartListener((widget) -> widget.setConsumerOffset(offsetX, offsetY))
                .setDragMoveListener((widget, value) -> {
                    this.offsetX = value.getNewX();
                    this.offsetY = value.getNewY();
                })
                .setDragStopListener((widget) -> {
                    record.overlayPositionX = this.offsetX;
                    record.overlayPositionY = this.offsetY;
                    Controller.setRCraft(record, itemStack, slot);
                });

        invE.setListener(i -> {
            record.inventory = false;
            Controller.setRCraft(record, itemStack, slot);
            remove(invE);
            add(invD);
            refreshCalculator();
        });

        invD.setListener(i -> {
            record.inventory = true;
            Controller.setRCraft(record, itemStack, slot);
            remove(invD);
            add(invE);
            refreshCalculator();
        });

        refreshRecent();
        setMode(record.mode);
    }

    void setMode(RecordCraft.Mode mode) {
        record.mode = mode;
        Controller.setRCraft(record, itemStack, slot);
        input.setDisabled(mode == INPUT);
        output.setDisabled(mode == OUTPUT);
        catalyst.setDisabled(mode == CATALYST);
        steps.setDisabled(mode == STEPS);
        refreshResult();
    }

    void refreshRecent() {
        label.setLabel(record.getLatest());
    }

    public void refreshCalculator() {
        try {
            String s = amount.getText();
            long i = s.isEmpty() ? 1 : Long.parseLong(amount.getText());
            amount.setColor(JecaGui.COLOR_TEXT_WHITE);
            List<ILabel> dest = Collections.singletonList(label.getLabel().copy().setAmount(i));
            CostList list = record.inventory ? new CostList(getInventory(), dest) : new CostList(dest);
            calculator = list.calculate();
        } catch (NumberFormatException | ArithmeticException e) {
            amount.setColor(JecaGui.COLOR_TEXT_RED);
            calculator = null;
        }
        refreshResult();
    }

    List<ILabel> getInventory() {
        Inventory inv = getPlayer().getInventory();
        ArrayList<ILabel> labels = new ArrayList<>();
        Consumer<List<ItemStack>> add = i -> i.stream()
                .filter(j -> !j.isEmpty())
                .forEach(j -> labels.add(ILabel.Converter.from(j)));

        add.accept(inv.armor);
        add.accept(inv.items);
        add.accept(inv.offhand);
        return labels;
    }

    void refreshResult() {
        if (calculator == null) {
            result.setLabels(new ArrayList<>());
        } else {
            switch (record.mode) {
                case INPUT -> result.setLabels(calculator.getInputs());
                case OUTPUT -> result.setLabels(calculator.getOutputs(getInventory()));
                case CATALYST -> result.setLabels(calculator.getCatalysts());
                case STEPS -> result.setLabels(calculator.getSteps());
            }
        }
    }

    private void refreshLabel(ILabel l) {
        record.push(l, false);
        Controller.setRCraft(record, itemStack, slot);
        refreshCalculator();
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!mouseIn(xMouse, yMouse)) {
            return false;
        }

        if (windowFocusListener != null) {
            windowFocusListener.invoke(this);
        }

        super.onMouseClicked(gui, xMouse, yMouse, button);
        return true;
    }

    protected boolean mouseIn(int xMouse, int yMouse) {
        return JecaGui.mouseIn(offsetX, offsetY, WINDOW_WIDTH, WINDOW_HEIGHT, xMouse, yMouse);
    }

    public GuiCraftMini setOnFocusListener(ListenerAction<? super GuiCraftMini> windowFocusListener) {
        this.windowFocusListener = windowFocusListener;
        return this;
    }

    public GuiCraftMini setWindowCloseListener(ListenerAction<? super GuiCraftMini> windowCloseListener) {
        this.windowCloseListener = windowCloseListener;
        return this;
    }

    public void setDepth(int depth, boolean save) {
        if (this.record.overlayDepth != depth) {
            record.overlayDepth = depth;
            if (save) {
                Controller.setRCraft(record, itemStack, slot);
            }
        }
    }

    public void setDepth(int depth) {
        setDepth(depth, false);
    }

    public int getDepth() {
        return record.overlayDepth;
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getWidth() {
        return this.WINDOW_WIDTH;
    }

    public int getHeight() {
        return this.WINDOW_HEIGHT;
    }
}
