package me.towdium.jecalculation.gui.guis;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.RecordCraft;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
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
@SideOnly(Side.CLIENT)
public class GuiCraft extends WContainer implements IGui  {
    Mode mode = Mode.INPUT;
    CostList.Calculator calculator = null;
    RecordCraft record = Controller.getRCraft();
    WLabelGroup recent = new WLabelGroup(7, 31, 8, 1, WLabel.Mode.PICKER)
            .setListener((i, v) -> JecaGui.getCurrent().hand = i.get(v));
    WLabelScroll result = new WLabelScroll(7, 87, 8, 4, WLabel.Mode.RESULT, true);
    WButton steps = new WButtonIcon(64, 62, 20, 20, Resource.BTN_LIST, "craft.step")
            .setListener(i -> setMode(Mode.STEPS));
    WButton catalyst = new WButtonIcon(45, 62, 20, 20, Resource.BTN_CAT, "common.catalyst")
            .setListener(i -> setMode(Mode.CATALYST));
    WButton output = new WButtonIcon(26, 62, 20, 20, Resource.BTN_OUT, "common.output")
            .setListener(i -> setMode(Mode.OUTPUT));
    WButton input = new WButtonIcon(7, 62, 20, 20, Resource.BTN_IN, "common.input")
            .setListener(i -> setMode(Mode.INPUT));

    WLabel label = new WLabel(31, 7, 20, 20, WLabel.Mode.SELECTOR).setListener((i, v) -> refreshLabel(v, false, true));
    WButton invE = new WButtonIcon(149, 62, 20, 20, Resource.BTN_INV_E, "craft.inventory_enabled");
    WButton invD = new WButtonIcon(149, 62, 20, 20, Resource.BTN_INV_D, "craft.inventory_disabled");
    WTextField amount = new WTextField(60, 7, 65).setText(record.amount).setListener(i -> {
        record.amount = i.getText();
        Controller.setRCraft(record);
        refreshCalculator();
    });


    public GuiCraft() {
        add(new WHelp("craft"));
        add(new WPanel());
        add(new WButtonIcon(7, 7, 20, 20, Resource.BTN_LABEL, "craft.label")
                    .setListener(i -> JecaGui.displayGui(new GuiLabel(l -> {
                        JecaGui.displayParent();
                        JecaGui.getCurrent().hand = l;
                    }))));
        add(new WButtonIcon(130, 7, 20, 20, Resource.BTN_NEW, "craft.recipe")
                    .setListener(i -> JecaGui.displayGui(true, true, new GuiRecipe())));
        add(new WButtonIcon(149, 7, 20, 20, Resource.BTN_SEARCH, "craft.search")
                    .setListener(i -> JecaGui.displayGui(new GuiSearch())));
        add(new WText(53, 13, JecaGui.Font.PLAIN, "x"));
        add(new WLine(55));
        add(new WIcon(151, 31, 18, 18, Resource.ICN_RECENT, "craft.history"));
        add(recent, label, input, output, catalyst, steps, result, amount, record.inventory ? invE : invD);
        invE.setListener(i -> {
            record.inventory = false;
            Controller.setRCraft(record);
            remove(invE);
            add(invD);
            refreshCalculator();
        });
        invD.setListener(i -> {
            record.inventory = true;
            Controller.setRCraft(record);
            remove(invD);
            add(invE);
            refreshCalculator();
        });
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
        label.setLabel(record.getLatest());
        recent.setLabel(record.getHistory(), 0);
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
        InventoryPlayer inv = Minecraft.getMinecraft().thePlayer.inventory;
        ArrayList<ILabel> labels = new ArrayList<>();
        Consumer<Stream<ItemStack>> add = i -> i
                                              .filter(j -> !ItemStackHelper.isEmpty(j))
                                              .forEach(j -> labels.add(ILabel.Converter.from(j)));
        add.accept(Arrays.stream(inv.armorInventory));
        add.accept(Arrays.stream(inv.mainInventory));
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

    private void refreshLabel(ILabel l, boolean replace, boolean suggest) {
        boolean dup = record.push(l, replace);
        Controller.setRCraft(record);
        refreshRecent();
        refreshCalculator();
        if (suggest && findRecipe(l).isEmpty()) {
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
            if (!match.isEmpty()) add(new Suggest(list.size() > 3 ? list.subList(0, 3) : list, !dup));
        }
    }

    private static List<ILabel> findRecipe(ILabel l) {
        return Controller.recipeIterator().stream()
                         .map(i -> i.matches(l))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .collect(Collectors.toList());
    }

    enum Mode {
        INPUT, OUTPUT, CATALYST, STEPS
    }
    class Suggest extends WContainer {
        boolean replace;

        public Suggest(List<ILabel> labels, boolean replace) {
            this.replace = replace;
            int width = labels.size() * 20;
            add(new WPanel(0 - width, 2, 56 + width, 30));
            add(new WLabel(31, 7, 20, 20, WLabel.Mode.PICKER).setLabel(label.getLabel())
                                                             .setListener((i, v) -> refresh(v)));
            add(new WIcon(5 - width, 7, 18, 20, Resource.ICN_HELP, "craft.suggest"));
            add(new WLine(26, 7, 20, false));
            for (int i = 0; i < labels.size(); i++) {
                add(new WLabel(3 - i * 20, 7, 20, 20, WLabel.Mode.PICKER).setLabel(labels.get(i))
                                                                         .setListener((j, v) -> refresh(v)));
            }
        }

        public void refresh(ILabel l) {
            GuiCraft.this.remove(this);
            refreshLabel(l, replace, false);
        }

        @Override
        public boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
            if (!super.onClicked(gui, xMouse, yMouse, button)) GuiCraft.this.remove(this);
            return true;
        }

        @Override
        public boolean onKey(JecaGui gui, char ch, int code) {
            if (!super.onKey(gui, ch, code)) {
                if (code == Keyboard.KEY_ESCAPE) {
                    GuiCraft.this.remove(this);
                    return true;
                } else return false;
            } else return true;
        }
    }
}
