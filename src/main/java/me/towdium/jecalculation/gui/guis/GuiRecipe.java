package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.Recipe.IO;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.jei.JecaPlugin;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Trio;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_RED;
import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_WHITE;
import static me.towdium.jecalculation.gui.JecaGui.Font.PLAIN;
import static me.towdium.jecalculation.gui.Resource.*;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class GuiRecipe extends WContainer implements IGui {
    Pair<String, Integer> dest;
    HashMap<Integer, List<ILabel>> disambCache = new HashMap<>();
    WSwitcher group = new WSwitcher(7, 7, 162, Controller.getGroups()).setListener(i -> refresh());
    WTextField text = new WTextField(49, 32, 119);
    WLabelGroup catalyst = new WLabelGroup(29, 87, 7, 1, 20, 20, true, true);
    WLabelGroup input = new WLabelGroup(29, 111, 7, 2, 20, 20, true, true);
    WLabelGroup output = new WLabelGroup(29, 63, 7, 1, 20, 20, true, true);
    WButton clear = new WButtonIcon(64, 32, 20, 20, BTN_DEL, "recipe.clear").setListener(i -> reset());
    // check duplicate and valid
    WButton copy = new WButtonIcon(83, 32, 20, 20, BTN_COPY, "recipe.copy").setListener(i -> {
        Controller.addRecipe(group.getText(), toRecipe());
        JecaGui.displayParent();
    });
    WButton label = new WButtonIcon(45, 32, 20, 20, BTN_LABEL, "recipe.label").setListener(i ->
            JecaGui.displayGui(new GuiLabel((l) -> {
                JecaGui.displayParent();
                JecaGui.getCurrent().hand = l;
            })));
    WButton save = new WButtonIcon(26, 32, 20, 20, BTN_SAVE, "recipe.save").setDisabled(true).setListener(i -> {
        if (dest == null)
            Controller.addRecipe(group.getText(), toRecipe());
        else {
            String group = this.group.getText();
            if (group.equals(dest.one)) Controller.setRecipe(dest.one, dest.two, toRecipe());
            else Controller.setRecipe(group, dest.one, dest.two, toRecipe());
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
        fromRecipe(r);
        this.group.setIndex(Controller.getGroups().indexOf(group));
        refresh();
    }

    public GuiRecipe() {
        setup(input, 0);
        setup(catalyst, 14);
        setup(output, 21);
        add(new WHelp("recipe"), new WPanel());
        add(new WIcon(7, 63, 22, 20, ICN_OUTPUT, "common.output"));
        add(new WIcon(7, 87, 22, 20, ICN_CATALYST, "common.catalyst"));
        add(new WIcon(7, 111, 22, 40, ICN_INPUT, "common.input"));
        add(new WLine(57));
        add(input, catalyst, output, group);
        if (group.getTexts().isEmpty()) group.setText(Utilities.I18n.get("gui.common.default"));
        String last = Controller.getLast();
        int index = -1;
        if (last != null) index = group.getTexts().indexOf(last);
        if (index != -1) group.setIndex(index);
        setNewGroup(false);
        copy.setDisabled(true);
        text.setListener(i -> yes.setDisabled(i.getText().isEmpty()));
    }

    private void setup(WLabelGroup w, int offset) {
        w.setLsnrUpdate((i, v) -> {
            disambCache.remove(v + offset);
            refresh();
        }).setLsnrClick((i, v) -> {
            ILabel l = i.get(v).getLabel();
            if (l != ILabel.EMPTY) add(new WAmount(i.get(v), v + offset));
        });
    }

    @Override
    public boolean onKeyPressed(JecaGui gui, int key, int modifier) {
        if (key == GLFW.GLFW_KEY_ESCAPE && contains(text)) {
            setNewGroup(false);
            return true;
        }
        return super.onKeyPressed(gui, key, modifier);
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        WLabel w = getLabelUnderMouse(xMouse, yMouse);
        if (w == null) return super.onMouseScroll(gui, xMouse, yMouse, diff);
        ILabel l = w.getLabel();
        for (int i = 0; i < Math.abs(diff); i++)
            l = diff > 0 ? l.increaseAmount() : l.decreaseAmount();
        w.setLabel(l);
        refresh();
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
        input.setLabel(Collections.nCopies(14, ILabel.EMPTY), 0);
        catalyst.setLabel(Collections.nCopies(7, ILabel.EMPTY), 0);
        output.setLabel(Collections.nCopies(7, ILabel.EMPTY), 0);
        disambCache.clear();
        refresh();
    }

    public void transfer(IRecipeLayout recipe) {
        // item disamb raw
        ArrayList<Trio<ILabel, CostList, CostList>> input = new ArrayList<>();
        ArrayList<Trio<ILabel, CostList, CostList>> output = new ArrayList<>();
        disambCache = new HashMap<>();

        // merge jei structure into list input/output
        Stream.of(recipe.getFluidStacks(), recipe.getItemStacks())
                .flatMap(i -> i.getGuiIngredients().values().stream())
                .forEach(i -> merge(i.isInput() ? input : output, i, recipe, i.isInput()));

        // convert catalyst
        List<ILabel> catalysts = JecaPlugin.runtime.getRecipeManager().getRecipeCatalysts(recipe.getRecipeCategory())
                .stream().map(ILabel.Converter::from).collect(Collectors.toList());
        if (catalysts.size() == 1) catalyst.setLabel(catalysts.get(0), 0);
        else if (catalysts.size() > 1) {
            catalyst.setLabel(ILabel.CONVERTER.first(catalysts, recipe), 0);
            disambCache.put(14, catalysts);
        }

        // generate disamb info according to content in list input/output
        this.input.setLabel(sort(input, 0), 0);
        this.output.setLabel(sort(output, 21), 0);
        refresh();
    }

    private void merge(ArrayList<Trio<ILabel, CostList, CostList>> dst, IGuiIngredient<?> gi, IRecipeLayout context, boolean input) {
        List<ILabel> list = gi.getAllIngredients().stream().map(ILabel.Converter::from).collect(Collectors.toList());
        if (list.isEmpty()) return;
        dst.stream().filter(p -> {
            CostList cl = new CostList(list);
            if (p.three.equals(cl)) {
                ILabel.MERGER.merge(p.one, ILabel.CONVERTER.first(list, context)).ifPresent(i -> p.one = i);
                p.two = p.two.merge(cl, true, false);
                return true;
            } else return false;
        }).findAny().orElseGet(() -> {
            ILabel rep = ILabel.CONVERTER.first(list, context);
            if (!input && list.size() == 1) rep = list.get(0).copy();
            Trio<ILabel, CostList, CostList> ret = new Trio<>(
                    rep, new CostList(list), new CostList(list));
            dst.add(ret);
            return ret;
        });
    }

    private ArrayList<ILabel> sort(ArrayList<Trio<ILabel, CostList, CostList>> src, int offset) {
        ArrayList<ILabel> ret = new ArrayList<>();
        for (int i = 0; i < src.size(); i++) {
            Trio<ILabel, CostList, CostList> p = src.get(i);
            ret.add(p.one);
            if (p.two.getLabels().size() > 1) disambCache.put(i + offset, p.two.getLabels());
        }
        return ret;
    }

    private Recipe toRecipe() {
        return new Recipe(input.getLabels(), catalyst.getLabels(), output.getLabels());
    }

    void fromRecipe(Recipe r) {
        input.setLabel(Arrays.stream(r.getLabel(IO.INPUT))
                .map(ILabel::copy).collect(Collectors.toList()), 0);
        catalyst.setLabel(Arrays.stream(r.getLabel(IO.CATALYST))
                .map(ILabel::copy).collect(Collectors.toList()), 0);
        output.setLabel(Arrays.stream(r.getLabel(IO.OUTPUT))
                .map(ILabel::copy).collect(Collectors.toList()), 0);
    }

    void refresh() {
        try {
            Recipe r = toRecipe();
            boolean d = dest == null ? Controller.hasDuplicate(r) :
                    Controller.hasDuplicate(r, dest.one, dest.two);
            save.setDisabled(d);
            if (dest != null) copy.setDisabled(d);
        } catch (IllegalArgumentException e) {
            save.setDisabled(true);
            copy.setDisabled(true);
        }
    }

    class WAmount extends WOverlay {
        WLabel temp;
        WButton number;
        WTextField text;
        WButton percent;
        WButton pick;
        WButton yes;
        WButton no;
        WLabel ref;

        public WAmount(WLabel w, int index) {
            ref = w;
            number = new WButtonText(ref.xPos + ref.xSize + 60, ref.yPos, 20, 20, "#", "general.to_percent")
                    .setListener(i -> {
                        temp.getLabel().setPercent(true);
                        update();
                    });
            percent = new WButtonText(ref.xPos + ref.xSize + 60, ref.yPos, 20, 20, "%", "general.to_percent")
                    .setListener(i -> {
                        temp.getLabel().setPercent(false);
                        update();
                    });
            temp = new WLabel(ref.xPos, ref.yPos, ref.xSize, ref.ySize, false, true).setLsnrUpdate((i, v) -> update());
            temp.setLabel(ref.getLabel().copy());
            add(new WPanel(ref.xPos - 5, ref.yPos - 5, ref.xSize + 152, ref.ySize + 10));
            add(new WText(ref.xPos + ref.xSize + 3, ref.yPos + 5, PLAIN, "x"));
            text = new WTextField(ref.xPos + ref.xSize + 10, ref.yPos + ref.ySize / 2 - WTextField.HEIGHT / 2, 50);
            pick = new WButtonIcon(ref.xPos + ref.xSize + 83, ref.yPos, 20, 20, BTN_PICK, "label.pick")
                    .setListener(i -> {
                        JecaGui.getCurrent().hand = temp.getLabel();
                        set(ILabel.EMPTY, index);
                    });
            yes = new WButtonIcon(ref.xPos + ref.xSize + 102, ref.yPos, 20, 20, BTN_YES, "label.confirm")
                    .setListener(i -> set(temp.getLabel(), index));
            no = new WButtonIcon(ref.xPos + ref.xSize + 121, ref.yPos, 20, 20, BTN_NO, "label.delete")
                    .setListener(i -> set(ILabel.EMPTY, index));
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
                temp.setLabel(temp.getLabel().setAmount(amount));
            });
            update();

        }

        private void set(ILabel l, int idx) {
            ref.setLabel(l);
            refresh();
            disambCache.remove(idx);
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
