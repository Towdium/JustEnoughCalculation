package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.CostList.Calculator;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiCalculator extends WContainer implements IGui {
    enumMode mode = enumMode.INPUT;
    Calculator calculator = null;
    WLabelGroup recent = new WLabelGroup(7, 31, 8, 1, WLabel.enumMode.PICKER);
    WLabel label = new WLabel(31, 7, 20, 20, WLabel.enumMode.SELECTOR);
    WLabelScroll result = new WLabelScroll(7, 87, 8, 4, WLabel.enumMode.RESULT, true);
    WButton input = new WButtonIcon(7, 62, 20, 20, Resource.BTN_IN, "calculator.input")
            .setLsnrLeft(() -> setMode(enumMode.INPUT));
    WButton output = new WButtonIcon(26, 62, 20, 20, Resource.BTN_OUT, "calculator.output")
            .setLsnrLeft(() -> setMode(enumMode.OUTPUT));
    WButton catalyst = new WButtonIcon(45, 62, 20, 20, Resource.BTN_CAT, "calculator.catalyst")
            .setLsnrLeft(() -> setMode(enumMode.CATALYST));
    WButton steps = new WButtonIcon(64, 62, 20, 20, Resource.BTN_LIST, "calculator.step")
            .setLsnrLeft(() -> setMode(enumMode.STEPS));
    WTextField amount = new WTextField(60, 7, 65);

    public GuiCalculator() {
        amount.setText(Controller.getAmount());
        label.setLsnrUpdate(() -> {
            Controller.setRecent(label.label);
            refreshRecent();
            refreshCalculator();
        });
        recent.setLsnrUpdate(l -> JecaGui.getCurrent().hand = recent.getLabelAt(l));
        amount.setLsnrText(s -> {
            Controller.setAmount(s);
            refreshCalculator();
        });
        add(new WHelp("calculator"));
        add(new WPanel());
        add(new WButtonIcon(7, 7, 20, 20, Resource.BTN_LABEL, "calculator.label")
                .setLsnrLeft(() -> JecaGui.displayGui(new GuiLabel(l -> {
                    JecaGui.displayParent();
                    JecaGui.getCurrent().hand = l;
                }))));
        add(new WButtonIcon(130, 7, 20, 20, Resource.BTN_NEW, "calculator.recipe")
                .setLsnrLeft(() -> JecaGui.displayGui(true, true, new GuiRecipe())));
        add(new WButtonIcon(149, 7, 20, 20, Resource.BTN_SEARCH, "calculator.search")
                .setLsnrLeft(() -> JecaGui.displayGui(new GuiSearch())));
        add(new WText(53, 13, JecaGui.Font.PLAIN, "x"));
        add(new WLine(55));
        add(new WIcon(151, 31, 18, 18, Resource.ICN_RECENT, "calculator.history"));
        addAll(recent, label, input, output, catalyst, steps, result, amount);
        refreshRecent();
        setMode(enumMode.INPUT);
    }

    @Override
    public void onVisible(JecaGui gui) {
        refreshCalculator();
    }

    void setMode(enumMode mode) {
        this.mode = mode;
        input.setDisabled(mode == enumMode.INPUT);
        output.setDisabled(mode == enumMode.OUTPUT);
        catalyst.setDisabled(mode == enumMode.CATALYST);
        steps.setDisabled(mode == enumMode.STEPS);
        refreshResult();
    }

    void refreshRecent() {
        List<ILabel> recent = Controller.getRecent();
        if (!recent.isEmpty()) label.setLabel(recent.get(0));
        if (recent.size() > 1) this.recent.setLabel(recent.subList(1, recent.size()), 0);
    }

    void refreshCalculator() {
        try {
            String s = amount.getText();
            int i = s.isEmpty() ? 1 : Integer.parseInt(amount.getText());
            amount.setColor(JecaGui.COLOR_TEXT_WHITE);
            List<ILabel> dest = Collections.singletonList(label.getLabel().copy().setAmount(i));
            calculator = new CostList(getInventory(), dest).calculate();
        } catch (NumberFormatException e) {
            amount.setColor(JecaGui.COLOR_TEXT_RED);
            calculator = null;
        }
        refreshResult();
    }

    List<ILabel> getInventory() {
        InventoryPlayer inv = Minecraft.getMinecraft().player.inventory;
        ArrayList<ILabel> labels = new ArrayList<>();
        Consumer<List<ItemStack>> add = i -> i.stream()
                .filter(j -> !j.isEmpty())
                .forEach(j -> labels.add(ILabel.Converter.from(j)));

        add.accept(inv.armorInventory);
        add.accept(inv.mainInventory);
        add.accept(inv.offHandInventory);
        return labels;
    }

    void refreshResult() {
        if (calculator == null) {
            result.setLabels(new ArrayList<>());
        } else {
            switch (mode) {
                case INPUT:
                    result.setLabels(calculator.getInputs());
                    break;
                case OUTPUT:
                    result.setLabels(calculator.getOutputs(getInventory()));
                    break;
                case CATALYST:
                    result.setLabels(calculator.getCatalysts());
                    break;
                case STEPS:
                    result.setLabels(calculator.getSteps());
                    break;
            }
        }
    }

    enum enumMode {
        INPUT, OUTPUT, CATALYST, STEPS
    }
}
