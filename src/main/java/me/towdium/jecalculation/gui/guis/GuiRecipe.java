package me.towdium.jecalculation.gui.guis;

import codechicken.nei.PositionedStack;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static me.towdium.jecalculation.gui.Resource.*;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
public class GuiRecipe extends WContainer implements IGui {
    Pair<String, Integer> dest;
    HashMap<Integer, List<ILabel>> disambiguation = new HashMap<>();
    WSwitcher switcherGroup = new WSwitcher(7, 7, 162, Controller.getGroups());
    WTextField textField = new WTextField(49, 31, 119);
    WLabelGroup groupCatalyst = new WLabelGroup(29, 87, 7, 1, 20, 20, WLabel.Mode.EDITOR).setListener((i, v) -> {
        disambiguation.remove(v + 14);
        refresh();
    });
    WLabelGroup groupInput = new WLabelGroup(29, 111, 7, 2, 20, 20, WLabel.Mode.EDITOR).setListener((i, v) -> {
        disambiguation.remove(v);
        refresh();
    });
    WLabelGroup groupOutput = new WLabelGroup(29, 63, 7, 1, 20, 20, WLabel.Mode.EDITOR).setListener((i, v) -> {
        disambiguation.remove(v + 21);
        refresh();
    });
    WButton buttonDisamb = new WButtonIcon(121, 31, 20, 20, BTN_DISAMB, "recipe.disamb").setListener(i -> {
        if (disambiguation != null) JecaGui.displayGui(new GuiDisambiguation(new ArrayList<>(disambiguation.values()))
                                                               .setCallback(l -> {
                                                                   JecaGui.displayParent();
                                                                   JecaGui.getCurrent().hand = l;
                                                               }));
    });
    WButton buttonClear = new WButtonIcon(64, 31, 20, 20, BTN_DEL, "recipe.clear").setListener(i -> clear());
    WButton buttonCopy = new WButtonIcon(83, 31, 20, 20, BTN_COPY, "recipe.copy").setListener(i -> {
        Controller.addRecipe(switcherGroup.getText(), toRecipe());
        JecaGui.displayParent();
    });
    WButton buttonLabel = new WButtonIcon(45, 31, 20, 20, BTN_LABEL, "recipe.label").setListener(i ->
                                                                                                         JecaGui.displayGui(new GuiLabel((l) -> {
                                                                                                             JecaGui.displayParent();
                                                                                                             JecaGui.getCurrent().hand = l;
                                                                                                         })));
    WButton buttonSave = new WButtonIcon(26, 31, 20, 20, BTN_SAVE, "recipe.save").setDisabled(true).setListener(i -> {
        if (dest == null)
            Controller.addRecipe(switcherGroup.getText(), toRecipe());
        else {
            if (textField.getText().equals(dest.one))
                Controller.setRecipe(dest.one, dest.two, toRecipe());
            else {
                Controller.removeRecipe(dest.one, dest.two);
                Controller.addRecipe(switcherGroup.getText(), toRecipe());
            }
        }
        JecaGui.displayParent();
    });
    WButton buttonDel = new WButtonIcon(102, 31, 20, 20, BTN_NO, "recipe.delete").setListener(i -> {
        Controller.removeRecipe(dest.one, dest.two);
        JecaGui.displayParent();
    });
    WButton buttonYes = new WButtonIcon(7, 31, 20, 20, BTN_YES, "recipe.confirm").setDisabled(true).setListener(i -> {
        switcherGroup.setTemp(textField.getText());
        textField.setText("");
        setNewGroup(false);
    });
    WButton buttonNo = new WButtonIcon(26, 31, 20, 20, BTN_NO, "common.cancel").setListener(i -> setNewGroup(false));
    WButton buttonNew = new WButtonIcon(7, 31, 20, 20, BTN_NEW, "recipe.new").setListener(i -> setNewGroup(true));

    public GuiRecipe(String group, int index) {
        this();
        dest = new Pair<>(group, index);
        Recipe r = Controller.getRecipe(group, index);
        fromRecipe(r);
        switcherGroup.setIndex(Controller.getGroups().indexOf(group));
        buttonCopy.setDisabled(false);
        buttonDel.setDisabled(false);
        buttonSave.setDisabled(false);
    }

    public GuiRecipe() {
        add(new WHelp("recipe"), new WPanel());
        add(new WIcon(7, 63, 22, 20, ICN_OUTPUT, "common.output"));
        add(new WIcon(7, 87, 22, 20, ICN_CATALYST, "common.catalyst"));
        add(new WIcon(7, 111, 22, 40, ICN_INPUT, "common.input"));
        add(new WLine(57));
        add(groupInput, groupCatalyst, groupOutput, switcherGroup);
        if (switcherGroup.getTexts().isEmpty()) switcherGroup.setTemp(Utilities.I18n.get("gui.common.default"));
        setNewGroup(false);
        buttonCopy.setDisabled(true);
        buttonDel.setDisabled(true);
        buttonDisamb.setDisabled(true);
        textField.setListener(i -> buttonYes.setDisabled(i.getText().isEmpty()));
    }

    public void setNewGroup(boolean b) {
        if (b) {
            remove(buttonNew, buttonLabel, buttonClear, buttonCopy, buttonSave, buttonDisamb, buttonDel);
            add(buttonYes, buttonNo, textField);
        } else {
            add(buttonNew, buttonLabel, buttonClear, buttonCopy, buttonSave, buttonDisamb, buttonDel);
            remove(buttonYes, buttonNo, textField);
        }
    }

    public void clear() {
        groupInput.setLabel(Collections.nCopies(14, ILabel.EMPTY), 0);
        groupCatalyst.setLabel(Collections.nCopies(7, ILabel.EMPTY), 0);
        groupOutput.setLabel(Collections.nCopies(7, ILabel.EMPTY), 0);
    }

    public void transfer(IRecipeHandler recipe, int recipeIndex) {
        // item disamb raw
        ArrayList<Trio<ILabel, CostList, CostList>> input = new ArrayList<>();
        ArrayList<Trio<ILabel, CostList, CostList>> output = new ArrayList<>();
        disambiguation = new HashMap<>();

        // input
        List<ItemStack> inputItemStacks = recipe.getIngredientStacks(recipeIndex).stream()
                                           .map((positionedStack) -> positionedStack.item).collect(Collectors.toList());
        List<ItemStack> outputItemStacks = Collections.singletonList(recipe.getResultStack(recipeIndex).item);

        merge(input, inputItemStacks, recipe);
        merge(output, outputItemStacks, recipe);

        // catalyst ignore multiple catalyst
        NEIPlugin.getCatalyst(recipe).map(ILabel.Converter::from).ifPresent(catalyst -> {
            groupCatalyst.setLabel(catalyst, 0);
        });

        groupInput.setLabel(sort(input, 0), 0);
        groupOutput.setLabel(sort(output, 21), 0);
        refresh();
    }

    private void merge(ArrayList<Trio<ILabel, CostList, CostList>> dst, List<ItemStack> gi, IRecipeHandler context) {
        List<ILabel> list = gi.stream().map(ILabel.Converter::from).collect(Collectors.toList());
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
            if (p.two.getLabels().size() > 1) disambiguation.put(i + offset, p.two.getLabels());
        }
        return ret;
    }

    private Recipe toRecipe() {
        return new Recipe(groupInput.getLabels(), groupCatalyst.getLabels(), groupOutput.getLabels());
    }

    void fromRecipe(Recipe r) {
        groupInput.setLabel(Arrays.stream(r.getLabel(Recipe.IO.INPUT))
                                  .map(ILabel::copy).collect(Collectors.toList()), 0);
        groupCatalyst.setLabel(Arrays.stream(r.getLabel(Recipe.IO.CATALYST))
                                     .map(ILabel::copy).collect(Collectors.toList()), 0);
        groupOutput.setLabel(Arrays.stream(r.getLabel(Recipe.IO.OUTPUT))
                                   .map(ILabel::copy).collect(Collectors.toList()), 0);
    }


    void refresh() {
        buttonDisamb.setDisabled(disambiguation.isEmpty());
        try {
            Recipe r = toRecipe();
            buttonSave.setDisabled(Controller.hasDuplicate(r));
        } catch (IllegalArgumentException e) {
            buttonSave.setDisabled(true);
        }
    }
}
