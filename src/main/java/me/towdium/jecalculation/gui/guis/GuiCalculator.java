package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.CostList.Calculator;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiCalculator extends WContainer implements IGui {
    Mode mode = Mode.INPUT;
    Calculator calculator = null;
    WLabelGroup recent = new WLabelGroup(7, 31, 8, 1, WLabel.Mode.PICKER)
            .setListener((i, v) -> JecaGui.getCurrent().hand = i.get(v));
    WLabelScroll result = new WLabelScroll(7, 87, 8, 4, WLabel.Mode.RESULT, true);
    WButton steps = new WButtonIcon(64, 62, 20, 20, Resource.BTN_LIST, "calculator.step")
            .setListener(i -> setMode(Mode.STEPS));
    WButton catalyst = new WButtonIcon(45, 62, 20, 20, Resource.BTN_CAT, "common.catalyst")
            .setListener(i -> setMode(Mode.CATALYST));
    WButton output = new WButtonIcon(26, 62, 20, 20, Resource.BTN_OUT, "common.output")
            .setListener(i -> setMode(Mode.OUTPUT));
    WButton input = new WButtonIcon(7, 62, 20, 20, Resource.BTN_IN, "common.input")
            .setListener(i -> setMode(Mode.INPUT));
    WTextField amount = new WTextField(60, 7, 65).setText(Controller.getAmount()).setListener(i -> {
        Controller.setAmount(i.getText());
        refreshCalculator();
    });
    WLabel label = new WLabel(31, 7, 20, 20, WLabel.Mode.SELECTOR).setListener((i, v) -> refreshLabel(v, false));

    private void refreshLabel(ILabel l, boolean replace) {
        Controller.setRecent(l, replace);
        refreshRecent();
        refreshCalculator();
        if (findRecipe(l).isEmpty()) {
            Pair<List<ILabel>, List<ILabel>> guess = ILabel.CONVERTER.guess(Collections.singletonList(l));
            LinkedHashSet<ILabel> match = new LinkedHashSet<>();
            List<ILabel> fuzzy = new ArrayList<>();
            Stream.of(guess.one, guess.two).flatMap(Collection::stream).forEach(i -> {
                List<ILabel> list = findRecipe(i);
                list.forEach(j -> match.add(j.setPercent(false).setAmount(1)));
                if (!list.isEmpty()) fuzzy.add(i);
            });
            match.addAll(fuzzy);
            List<ILabel> list = new ArrayList<>(match);
            if (!match.isEmpty()) add(new Suggest(list.size() > 3 ? list.subList(0, 3) : list));
        }
    }

    private static List<ILabel> findRecipe(ILabel l) {
        return Controller.recipeIterator().stream()
                .map(i -> i.matches(l))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public GuiCalculator() {
        add(new WHelp("calculator"));
        add(new WPanel());
        add(new WButtonIcon(7, 7, 20, 20, Resource.BTN_LABEL, "calculator.label")
                .setListener(i -> JecaGui.displayGui(new GuiLabel(l -> {
                    JecaGui.displayParent();
                    JecaGui.getCurrent().hand = l;
                }))));
        add(new WButtonIcon(130, 7, 20, 20, Resource.BTN_NEW, "calculator.recipe")
                .setListener(i -> JecaGui.displayGui(true, true, new GuiRecipe())));
        add(new WButtonIcon(149, 7, 20, 20, Resource.BTN_SEARCH, "calculator.search")
                .setListener(i -> JecaGui.displayGui(new GuiSearch())));
        add(new WText(53, 13, JecaGui.Font.PLAIN, "x"));
        add(new WLine(55));
        add(new WIcon(151, 31, 18, 18, Resource.ICN_RECENT, "calculator.history"));
        add(recent, label, input, output, catalyst, steps, result, amount);
        refreshRecent();
        setMode(Mode.INPUT);
    }

    @Override
    public void onVisible(JecaGui gui) {
        refreshCalculator();
    }

    void setMode(Mode mode) {
        this.mode = mode;
        input.setDisabled(mode == Mode.INPUT);
        output.setDisabled(mode == Mode.OUTPUT);
        catalyst.setDisabled(mode == Mode.CATALYST);
        steps.setDisabled(mode == Mode.STEPS);
        refreshResult();
    }

    void refreshRecent() {
        List<ILabel> labels = Controller.getRecent();
        label.setLabel(labels.isEmpty() ? ILabel.EMPTY : labels.get(0));
        recent.setLabel(labels.isEmpty() ? new ArrayList<>() : labels.subList(1, labels.size()), 0);
    }

    void refreshCalculator() {
        try {
            String s = amount.getText();
            long i = s.isEmpty() ? 1 : Long.parseLong(amount.getText());
            amount.setColor(JecaGui.COLOR_TEXT_WHITE);
            List<ILabel> dest = Collections.singletonList(label.getLabel().copy().setAmount(i));
            calculator = new CostList(getInventory(), dest).calculate();
        } catch (NumberFormatException | ArithmeticException e) {
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

    enum Mode {
        INPUT, OUTPUT, CATALYST, STEPS
    }

    class Suggest extends WContainer {
        public Suggest(List<ILabel> labels) {
            int width = labels.size() * 20;
            add(new WPanel(0 - width, 2, 56 + width, 30));
            add(new WLabel(31, 7, 20, 20, WLabel.Mode.SELECTOR).setLabel(label.getLabel())
                    .setListener((i, v) -> refresh(v)));
            add(new WIcon(5 - width, 7, 18, 20, Resource.ICN_HELP, "calculator.suggest"));
            add(new WLine(26, 7, 20, false));
            for (int i = 0; i < labels.size(); i++) {
                add(new WLabel(3 - i * 20, 7, 20, 20, WLabel.Mode.PICKER).setLabel(labels.get(i))
                        .setListener((j, v) -> refresh(v)));
            }
        }

        public void refresh(ILabel l) {
            GuiCalculator.this.remove(this);
            refreshLabel(l, true);
        }

        @Override
        public boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
            if (!super.onClicked(gui, xMouse, yMouse, button)) GuiCalculator.this.remove(this);
            return true;
        }

        @Override
        public boolean onKey(JecaGui gui, char ch, int code) {
            if (!super.onKey(gui, ch, code)) {
                if (code == Keyboard.KEY_ESCAPE) {
                    GuiCalculator.this.remove(this);
                    return true;
                } else return false;
            } else return true;
        }
    }
}
