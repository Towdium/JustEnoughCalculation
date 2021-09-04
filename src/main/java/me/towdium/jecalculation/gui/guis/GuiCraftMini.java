package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.CostList.Calculator;
import me.towdium.jecalculation.data.structure.RecordCraft;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.jei.JecaPlugin;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;

import static me.towdium.jecalculation.data.structure.RecordCraft.Mode.*;
import static me.towdium.jecalculation.utils.Utilities.getPlayer;

/**
 * Author: ekgame
 * Date:   9/04/21.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class GuiCraftMini extends WContainer {
    ItemStack itemStack;
    Calculator calculator = null;
    RecordCraft record;

    WLabel label = new WLabel(4, 16, 20, 20, true)
            .setLsnrUpdate((i, v) -> refreshLabel(v));

    WTextField amount = new WTextField(31, 16, 35).setListener(i -> {
        record.amount = i.getText();
        Controller.setRCraft(record, itemStack);
        refreshCalculator();
    });

    WButton invE = new WButtonIcon(67, 16, 20, 20, Resource.BTN_INV_E, "craft.inventory_enabled");
    WButton invD = new WButtonIcon(67, 16, 20, 20, Resource.BTN_INV_D, "craft.inventory_disabled");

    WButton input = new WButtonIcon(4, 37, 20, 20, Resource.BTN_IN, "common.input")
            .setListener(i -> setMode(INPUT));

    WButton output = new WButtonIcon(25, 37, 20, 20, Resource.BTN_OUT, "craft.output")
            .setListener(i -> setMode(OUTPUT));

    WButton catalyst = new WButtonIcon(46, 37, 20, 20, Resource.BTN_CAT, "common.catalyst")
            .setListener(i -> setMode(CATALYST));

    WButton steps = new WButtonIcon(67, 37, 20, 20, Resource.BTN_LIST, "craft.step")
            .setListener(i -> setMode(STEPS));

    WLabelScroll result = new WLabelScroll(4, 58, 4, 2, false)
            .setLsnrClick((i, v) -> JecaPlugin.showRecipe(i.get(v).getLabel()))
            .setFmtAmount(i -> i.getAmountString(true))
            .setFmtTooltip((i, j) -> i.getToolTip(j, true));

    public GuiCraftMini(@Nullable ItemStack is) {
        itemStack = is;
        record = Controller.getRCraft(is);
        amount.setText(record.amount);

        add(new WPanel(0, 0, 98, 98));
        add(new WDrag(4, 4, 78, 11));
        add(new WButtonText(83, 4, 11, 11, "x"));

        add(new WText(25, 21, JecaGui.Font.RAW, "x"));
        add(label, input, output, catalyst, steps, result, amount, record.inventory ? invE : invD);

        invE.setListener(i -> {
            record.inventory = false;
            Controller.setRCraft(record, itemStack);
            remove(invE);
            add(invD);
            refreshCalculator();
        });

        invD.setListener(i -> {
            record.inventory = true;
            Controller.setRCraft(record, itemStack);
            remove(invD);
            add(invE);
            refreshCalculator();
        });

        refreshRecent();
        setMode(record.mode);
        refreshCalculator();
    }

    void setMode(RecordCraft.Mode mode) {
        record.mode = mode;
        Controller.setRCraft(record, itemStack);
        input.setDisabled(mode == INPUT);
        output.setDisabled(mode == OUTPUT);
        catalyst.setDisabled(mode == CATALYST);
        steps.setDisabled(mode == STEPS);
        refreshResult();
    }

    void refreshRecent() {
        label.setLabel(record.getLatest());
    }

    void refreshCalculator() {
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
        PlayerInventory inv = getPlayer().inventory;
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
            switch (record.mode) {
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

    private void refreshLabel(ILabel l) {
        record.push(l, false);
        Controller.setRCraft(record, itemStack);
        refreshCalculator();
    }
}
