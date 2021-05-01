package me.towdium.jecalculation.gui.guis;

import codechicken.nei.recipe.IRecipeHandler;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.gui.Resource.*;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
@ParametersAreNonnullByDefault
public class GuiRecipe extends WContainer implements IGui {
    Pair<String, Integer> dest;
    HashMap<Integer, List<ILabel>> disambCache = new HashMap<>();
    WSwitcher group = new WSwitcher(7, 7, 162, Controller.getGroups()).setListener(i -> refresh());
    WTextField text = new WTextField(49, 32, 119);
    WLabelGroup catalyst = new WLabelGroup(29, 87, 7, 1, 20, 20, WLabel.Mode.EDITOR).setListener((i, v) -> {
        disambCache.remove(v + 14);
        refresh();
    });
    WLabelGroup input = new WLabelGroup(29, 111, 7, 2, 20, 20, WLabel.Mode.EDITOR).setListener((i, v) -> {
        disambCache.remove(v);
        refresh();
    });
    WLabelGroup output = new WLabelGroup(29, 63, 7, 1, 20, 20, WLabel.Mode.EDITOR).setListener((i, v) -> {
        disambCache.remove(v + 21);
        refresh();
    });
    WButton disamb = new WButtonIcon(102, 32, 20, 20, BTN_DISAMB, "recipe.disamb").setListener(i -> {
        if (disambCache != null)
            JecaGui.displayGui(new GuiDisamb(new ArrayList<>(disambCache.values())).setCallback(l -> {
                JecaGui.displayParent();
                JecaGui.getCurrent().hand = l;
            }));
    });
    WButton clear = new WButtonIcon(64, 32, 20, 20, BTN_DEL, "recipe.clear").setListener(i -> reset());
    // check duplicate and valid
    WButton copy = new WButtonIcon(83, 32, 20, 20, BTN_COPY, "recipe.copy").setListener(i -> {
        Controller.addRecipe(group.getText(), toRecipe());
        JecaGui.displayParent();
    });
    WButton label = new WButtonIcon(45, 32, 20, 20, BTN_LABEL, "recipe.label")
            .setListener(i -> JecaGui.displayGui(new GuiLabel((l) -> {
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
        fromRecipe(r);
        this.group.setIndex(Controller.getGroups().indexOf(group));
        refresh();
    }

    public GuiRecipe() {
        add(new WHelp("recipe"), new WPanel());
        add(new WIcon(7, 63, 22, 20, ICN_OUTPUT, "common.output"));
        add(new WIcon(7, 87, 22, 20, ICN_CATALYST, "common.catalyst"));
        add(new WIcon(7, 111, 22, 40, ICN_INPUT, "common.input"));
        add(new WLine(57));
        add(input, catalyst, output, group);
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
        disamb.setDisabled(true);
        text.setListener(i -> yes.setDisabled(i.getText().isEmpty()));
    }

    @Override
    public boolean onKey(JecaGui gui, char ch, int code) {
        if (code == Keyboard.KEY_ESCAPE && contains(text)) {
            setNewGroup(false);
            return true;
        }
        return super.onKey(gui, ch, code);
    }

    public void setNewGroup(boolean b) {
        if (b) {
            remove(neu, label, clear, copy, save, disamb);
            add(yes, no, text);
        } else {
            add(neu, label, clear, copy, save, disamb);
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

    public void transfer(IRecipeHandler recipe, int recipeIndex) {
        // item disamb raw
        ArrayList<Trio<ILabel, CostList, CostList>> input = new ArrayList<>();
        ArrayList<Trio<ILabel, CostList, CostList>> output = new ArrayList<>();
        disambCache = new HashMap<>();

        // input
        recipe.getIngredientStacks(recipeIndex).stream()
                                                  .map((positionedStack) -> positionedStack.items)
                                                  .forEach(i -> merge(input, Arrays.asList(i), recipe));
        // output
        merge(output, Arrays.asList(recipe.getResultStack(recipeIndex).items), recipe);

        // catalyst. Ignore multiple catalyst
        NEIPlugin.getCatalyst(recipe).map(ILabel.Converter::from).ifPresent(catalyst -> {
            this.catalyst.setLabel(catalyst, 0);
        });

        // other. Unused. For example fuel in furnaces.
        recipe.getOtherStacks(recipeIndex).stream();

        this.input.setLabel(sort(input, 0), 0);
        this.output.setLabel(sort(output, 21), 0);
        refresh();
    }

    private void merge(ArrayList<Trio<ILabel, CostList, CostList>> dst, List<ItemStack> gi, IRecipeHandler context) {
        List<ILabel> list = gi.stream().map(ILabel.Converter::from).filter(i -> i != ILabel.EMPTY)
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
            Trio<ILabel, CostList, CostList> ret = new Trio<>(ILabel.CONVERTER.first(list, context), new CostList(list),
                                                              new CostList(list));
            dst.add(ret);
            return ret;
        });
    }

    private ArrayList<ILabel> sort(ArrayList<Trio<ILabel, CostList, CostList>> src, int offset) {
        ArrayList<ILabel> ret = new ArrayList<>();
        for (int i = 0; i < src.size(); i++) {
            Trio<ILabel, CostList, CostList> p = src.get(i);
            ret.add(p.one);
            if (p.two.getLabels().size() > 1)
                disambCache.put(i + offset, p.two.getLabels());
        }
        return ret;
    }

    private Recipe toRecipe() {
        return new Recipe(input.getLabels(), catalyst.getLabels(), output.getLabels());
    }

    void fromRecipe(Recipe r) {
        input.setLabel(Arrays.stream(r.getLabel(Recipe.IO.INPUT)).map(ILabel::copy).collect(Collectors.toList()), 0);
        catalyst.setLabel(Arrays.stream(r.getLabel(Recipe.IO.CATALYST)).map(ILabel::copy).collect(Collectors.toList()),
                          0);
        output.setLabel(Arrays.stream(r.getLabel(Recipe.IO.OUTPUT)).map(ILabel::copy).collect(Collectors.toList()), 0);
    }


    void refresh() {
        disamb.setDisabled(disambCache.isEmpty());
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
}
