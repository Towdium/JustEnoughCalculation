package me.towdium.jecalculation.gui.guis;

import codechicken.nei.recipe.IRecipeHandler;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.Recipe.IO;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.nei.Adapter;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Trio;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_RED;
import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_WHITE;
import static me.towdium.jecalculation.gui.JecaGui.Font.PLAIN;
import static me.towdium.jecalculation.gui.Resource.*;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
@ParametersAreNonnullByDefault
public class GuiRecipe extends WContainer implements IGui {
    Pair<String, Integer> dest;
    //    HashMap<Integer, List<ILabel>> disambCache = new HashMap<>();
    WSwitcher group = new WSwitcher(7, 7, 162, Controller.getGroups()).setListener(i -> refresh());
    WTextField text = new WTextField(49, 32, 119);
    List<ILabel> input = new ArrayList<>();
    List<ILabel> output = new ArrayList<>();
    List<ILabel> catalyst = new ArrayList<>();
    WLabelGroup mid = new WLabelGroup(29, 87, 7, 1, 20, 20, true, true);
    WLabelGroup down = new WLabelGroup(29, 111, 7, 2, 20, 20, true, true);
    WLabelGroup up = new WLabelGroup(29, 63, 7, 1, 20, 20, true, true);
    //    WLabelGroup catalyst = new WLabelGroup(29, 87, 7, 1, 20, 20, WLabel.Mode.EDITOR).setListener((i, v) -> {
    //        disambCache.remove(v + 14);
    //        refresh();
    //    });
    //    WLabelGroup input = new WLabelGroup(29, 111, 7, 2, 20, 20, WLabel.Mode.EDITOR).setListener((i, v) -> {
    //        disambCache.remove(v);
    //        refresh();
    //    });
    //    WLabelGroup output = new WLabelGroup(29, 63, 7, 1, 20, 20, WLabel.Mode.EDITOR).setListener((i, v) -> {
    //        disambCache.remove(v + 21);
    //        refresh();
    //    });
    //    WButton disamb = new WButtonIcon(102, 32, 20, 20, BTN_DISAMB, "recipe.disamb").setListener(i -> {
    //        if (disambCache != null)
    //            JecaGui.displayGui(new GuiDisamb(new ArrayList<>(disambCache.values())).setCallback(l -> {
    //                JecaGui.displayParent();
    //                JecaGui.getCurrent().hand = l;
    //            }));
    //    });
    WButton clear = new WButtonIcon(64, 32, 20, 20, BTN_DEL, "recipe.clear").setListener(i -> reset());
    // check duplicate and valid
    WButton copy = new WButtonIcon(83, 32, 20, 20, BTN_COPY, "recipe.copy").setListener(i -> {
        Controller.addRecipe(group.getText(), toRecipe());
        JecaGui.displayParent();
    });
    WButton label = new WButtonIcon(45, 32, 20, 20, BTN_LABEL, "recipe.label").setListener(
            i -> JecaGui.displayGui(new GuiLabel((l) -> {
                JecaGui.displayParent();
                JecaGui.getCurrent().hand = l;
            })));
    WButton save = new WButtonIcon(26, 32, 20, 20, BTN_SAVE, "recipe.save").setDisabled(true).setListener(i -> {
        if (dest == null)
            Controller.addRecipe(group.getText(), toRecipe());
        else {
            String group = this.group.getText();
            if (group.equals(dest.one))
                Controller.setRecipe(dest.one, dest.two, toRecipe());
            else
                Controller.setRecipe(group, dest.one, dest.two, toRecipe());
        }
        JecaGui.displayParent();
    });
    WButton yes = new WButtonIcon(7, 32, 20, 20, BTN_YES, "recipe.confirm").setDisabled(true).setListener(i -> {
        group.setText(text.getText());
        text.setText("");
        setNewGroup(false);
        refresh();
    });
    WButton no = new WButtonIcon(26, 32, 20, 20, BTN_NO, "common.cancel").setListener(i -> setNewGroup(false));
    WButton neu = new WButtonIcon(7, 32, 20, 20, BTN_NEW, "recipe.new").setListener(i -> setNewGroup(true));

    public GuiRecipe(String group, int index) {
        this();
        dest = new Pair<>(group, index);
        Recipe r = Controller.getRecipe(group, index);
        //        fromRecipe(r);
        input = r.getLabel(IO.INPUT).stream().map(ILabel::copy).collect(Collectors.toList());
        catalyst = r.getLabel(IO.CATALYST).stream().map(ILabel::copy).collect(Collectors.toList());
        output = r.getLabel(IO.OUTPUT).stream().map(ILabel::copy).collect(Collectors.toList());
        this.group.setIndex(Controller.getGroups().indexOf(group));
        refresh();
    }

    public GuiRecipe() {
        for (IO i : IO.values())
            setup(getWidget(i), i);
        add(new WHelp("recipe"), new WPanel());
        add(new WIcon(7, 63, 22, 20, ICN_OUTPUT, "common.output"));
        add(new WIcon(7, 87, 22, 20, ICN_CATALYST, "common.catalyst"));
        add(new WIcon(7, 111, 22, 40, ICN_INPUT, "common.input"));
        add(new WLine(57));
        add(up, down, mid, group);
        if (group.getTexts().isEmpty())
            group.setText(Utilities.I18n.get("gui.common.default"));
        String last = Controller.getLast();
        int index = -1;
        if (last != null)
            index = group.getTexts().indexOf(last);
        if (index != -1)
            group.setIndex(index);
        setNewGroup(false);
        copy.setDisabled(true);
        //        disamb.setDisabled(true);
        text.setListener(i -> yes.setDisabled(i.getText().isEmpty()));
    }

    public List<ILabel> getLabel(IO type) {
        return Recipe.get(type, input, output, catalyst);
    }

    public WLabelGroup getWidget(IO type) {
        return Recipe.get(type, down, up, mid);
    }

    private void setup(WLabelGroup w, IO type) {
        w.setLsnrUpdate((i, v) -> {
            List<ILabel> ls = getLabel(type);
            while (ls.size() <= v)
                ls.add(ILabel.EMPTY);
            ls.set(v, i.get(v).getLabel());
            trim(ls);
            refresh();
        }).setLsnrClick((i, v) -> {
            ILabel l = i.get(v).getLabel();
            if (l != ILabel.EMPTY)
                add(new WAmount(i.get(v)));
        });
    }

    @Override
    public boolean onKeyPressed(JecaGui gui, char ch, int code) {
        if (code == Keyboard.KEY_ESCAPE && contains(text)) {
            setNewGroup(false);
            return true;
        }
        return super.onKeyPressed(gui, ch, code);
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        WLabel w = getLabelUnderMouse(xMouse, yMouse);
        if (w == null)
            return super.onMouseScroll(gui, xMouse, yMouse, diff);
        ILabel l = w.getLabel();
        for (int i = 0; i < Math.abs(diff); i++)
            l = diff > 0 ? l.increaseAmount() : l.decreaseAmount();
        w.setLabel(l, true);
        return true;
    }

    public void setNewGroup(boolean b) {
        if (b) {
            remove(neu, label, clear, copy, save);
            add(yes, no, text);
        } else {
            add(neu, label, clear, copy, save);
            remove(yes, no, text);
            text.setText("");
            yes.setDisabled(true);
        }
    }

    public void reset() {
        input = new ArrayList<>();
        catalyst = new ArrayList<>();
        output = new ArrayList<>();
        //        disambCache.clear();
        refresh();
    }

    public void transfer(IRecipeHandler recipe, int recipeIndex) {
        // item disamb raw
        ArrayList<Trio<ILabel, CostList, CostList>> input = new ArrayList<>();
        ArrayList<Trio<ILabel, CostList, CostList>> output = new ArrayList<>();
        //        disambCache = new HashMap<>();


        List<Object[]> recipeInputs = new ArrayList<>();
        List<Object[]> recipeOutputs = new ArrayList<>();
        Adapter.handleRecipe(recipe, recipeIndex, recipeInputs, recipeOutputs);

        // input
        recipeInputs.forEach(i -> merge(input, Arrays.asList(i), recipe, true));
        // output
        recipeOutputs.forEach(o -> merge(output, Arrays.asList(o), recipe, false));

        // catalyst. Ignore multiple catalyst
        this.catalyst = new ArrayList<>();
        NEIPlugin.getCatalyst(recipe)
                 .map(ILabel.Converter::from)
                 .ifPresent(catalyst -> this.catalyst.add(ILabel.CONVERTER.first(Collections.singletonList(catalyst), recipe)));

        // other. Unused. For example fuel in furnaces.
        //        recipe.getOtherStacks(recipeIndex).stream();

        this.input = extract(input);
        this.output = extract(output);
        refresh();
    }

    private void merge(ArrayList<Trio<ILabel, CostList, CostList>> dst,
                       List<Object> gi,
                       IRecipeHandler context,
                       boolean input) {
        List<ILabel> list = gi.stream()
                              .map(ILabel.Converter::from)
                              .filter(i -> i != ILabel.EMPTY)
                              .collect(Collectors.toList());
        if (list.isEmpty())
            return;
        dst.stream().filter(p -> {
            CostList cl = new CostList(list);
            if (p.three.equals(cl)) {
                ILabel.MERGER.merge(p.one, ILabel.CONVERTER.first(list, context)).ifPresent(i -> p.one = i);
                p.two = p.two.merge(cl, true, false);
                return true;
            } else
                return false;
        }).findAny().orElseGet(() -> {
            ILabel rep = ILabel.CONVERTER.first(list, context);
            if (!input && list.size() == 1)
                rep = list.get(0).copy();
            Trio<ILabel, CostList, CostList> ret = new Trio<>(rep, new CostList(list), new CostList(list));
            dst.add(ret);
            return ret;
        });
    }

    private ArrayList<ILabel> extract(ArrayList<Trio<ILabel, CostList, CostList>> src) {
        ArrayList<ILabel> ret = new ArrayList<>();
        for (int i = 0; i < src.size(); i++) {
            Trio<ILabel, CostList, CostList> p = src.get(i);
            ret.add(p.one);
            // TODO disamb
            // if (p.two.getLabels().size() > 1) disambCache.put(i + offset, p.two.getLabels());
        }
        return ret;
    }

    private void trim(List<ILabel> ls) {
        for (int i = ls.size() - 1; i >= 0; i--) {
            if (ls.get(i) == ILabel.EMPTY)
                ls.remove(i);
        }
    }

    private Recipe toRecipe() {  // TODO
        return new Recipe(input, catalyst, output);
    }

    void refresh() {
        up.setLabel(output, 0);
        mid.setLabel(catalyst, 0);
        down.setLabel(input, 0);

        try {
            Recipe r = toRecipe();
            boolean d = dest == null ? Controller.hasDuplicate(r) : Controller.hasDuplicate(r, dest.one, dest.two);
            save.setDisabled(d);
            if (dest != null)
                copy.setDisabled(d);
        } catch (IllegalArgumentException e) {
            save.setDisabled(true);
            copy.setDisabled(true);
        }
    }

    static class WAmount extends WOverlay {
        WLabel temp;
        WButton number;
        WTextField text;
        WButton percent;
        WButton pick;
        WButton yes;
        WButton no;
        WLabel ref;

        public WAmount(WLabel w) {
            ref = w;
            number = new WButtonText(ref.xPos + ref.xSize + 60, ref.yPos, 20, 20, "#",
                                     "general.to_percent").setListener(i -> {
                temp.getLabel().setPercent(true);
                update();
            });
            percent = new WButtonText(ref.xPos + ref.xSize + 60, ref.yPos, 20, 20, "%",
                                      "general.to_percent").setListener(i -> {
                temp.getLabel().setPercent(false);
                update();
            });
            temp = new WLabel(ref.xPos, ref.yPos, ref.xSize, ref.ySize, false, true).setLsnrUpdate((i, v) -> update());
            temp.setLabel(ref.getLabel().copy());
            add(new WPanel(ref.xPos - 5, ref.yPos - 5, ref.xSize + 152, ref.ySize + 10));
            add(new WText(ref.xPos + ref.xSize + 3, ref.yPos + 5, PLAIN, "x"));
            text = new WTextField(ref.xPos + ref.xSize + 10, ref.yPos + ref.ySize / 2 - WTextField.HEIGHT / 2, 50);
            pick = new WButtonIcon(ref.xPos + ref.xSize + 83, ref.yPos, 20, 20, BTN_PICK, "label.pick").setListener(
                    i -> {
                        JecaGui.getCurrent().hand = temp.getLabel();
                        set(ILabel.EMPTY);
                    });
            yes = new WButtonIcon(ref.xPos + ref.xSize + 102, ref.yPos, 20, 20, BTN_YES, "label.confirm").setListener(
                    i -> set(temp.getLabel()));
            no = new WButtonIcon(ref.xPos + ref.xSize + 121, ref.yPos, 20, 20, BTN_NO, "label.delete").setListener(
                    i -> set(ILabel.EMPTY));
            add(temp, text, pick, yes, no);
            text.setListener(i -> {
                boolean acceptable;
                long amount;
                try {
                    amount = Long.parseLong(text.getText());
                    acceptable = amount > 0;
                    if (!acceptable)
                        amount = 1;
                } catch (NumberFormatException e) {
                    acceptable = text.getText().isEmpty();
                    amount = 1;
                }
                text.setColor(acceptable ? COLOR_TEXT_WHITE : COLOR_TEXT_RED);
                yes.setDisabled(!acceptable);
                temp.setLabel(temp.getLabel().setAmount(amount));
            });
            update();

        }

        private void set(ILabel l) {
            ref.setLabel(l, true);
            //refresh();
            //disambCache.remove(idx);
            JecaGui.getCurrent().root.remove(this);
        }

        private void update() {
            number.setDisabled(!temp.getLabel().acceptPercent());
            if (temp.getLabel().isPercent()) {
                remove(number);
                add(percent);
            } else {
                remove(percent);
                add(number);
            }
            text.setText(Long.toString(temp.getLabel().getAmount()));
        }
    }
}
