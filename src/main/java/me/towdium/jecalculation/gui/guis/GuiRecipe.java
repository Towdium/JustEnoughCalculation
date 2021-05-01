package me.towdium.jecalculation.gui.guis;

import codechicken.nei.recipe.IRecipeHandler;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Triple;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
public class GuiRecipe extends WContainer implements IGui {
    Pair<String, Integer> dest;
    HashMap<Integer, List<ILabel>> disambiguation = new HashMap<>();
    WSwitcher switcherGroup = new WSwitcher(7, 7, 162, Controller.getGroups());
    WTextField textField = new WTextField(49, 33, 119);
    WButton buttonCopy = new WButtonIcon(83, 33, 20, 20, Resource.BTN_COPY, "recipe.copy").setLsnrLeft(() -> {
        Controller.addRecipe(switcherGroup.getText(), toRecipe());
        JecaGui.displayParent();
    });

    WLabelGroup groupCatalyst = new WLabelGroup(28, 87, 7, 1, 20, 20, WLabel.enumMode.EDITOR);
    WButton buttonDisamb = new WButtonIcon(121, 33, 20, 20, Resource.BTN_DISAMB, "recipe.disamb").setLsnrLeft(() -> {
        if (disambiguation != null) JecaGui.displayGui(new GuiDisambiguation(new ArrayList<>(disambiguation.values()))
                                                               .setCallback(l -> {
                                                                   JecaGui.displayParent();
                                                                   JecaGui.getCurrent().hand = l;
                                                               }));
    });    WLabelGroup groupInput = new WLabelGroup(28, 111, 7, 2, 20, 20, WLabel.enumMode.EDITOR).setLsnrUpdate(i -> {
        disambiguation.remove(i);
        refresh();
    });
    WLabelGroup groupOutput = new WLabelGroup(28, 63, 7, 1, 20, 20, WLabel.enumMode.EDITOR).setLsnrUpdate(i -> {
        disambiguation.remove(i + 21);
        refresh();
    });
    WButton buttonClear = new WButtonIcon(64, 33, 20, 20, Resource.BTN_DEL, "recipe.clear").setLsnrLeft(this::clear);
    WButton buttonLabel = new WButtonIcon(45, 33, 20, 20, Resource.BTN_LABEL, "recipe.label")
            .setLsnrLeft(() -> JecaGui.displayGui(new GuiLabel((l) -> {
                JecaGui.displayParent();
                JecaGui.getCurrent().hand = l;
            })));
    WButton buttonSave = new WButtonIcon(26, 33, 20, 20, Resource.BTN_SAVE, "recipe.save").setLsnrLeft(() -> {
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

    WButton buttonDel = new WButtonIcon(102, 33, 20, 20, Resource.BTN_NO, "recipe.delete").setLsnrLeft(() -> {
        Controller.removeRecipe(dest.one, dest.two);
        JecaGui.displayParent();
    });
    WButton buttonNew = new WButtonIcon(7, 33, 20, 20, Resource.BTN_NEW, "recipe.new")
            .setLsnrLeft(() -> setModeNewGroup(true));
    WButton buttonYes = new WButtonIcon(7, 33, 20, 20, Resource.BTN_YES, "recipe.confirm").setLsnrLeft(() -> {
        switcherGroup.setTemp(textField.getText());
        textField.setText("");
        setModeNewGroup(false);
    });
    WButton buttonNo = new WButtonIcon(26, 33, 20, 20, Resource.BTN_NO, "recipe.cancel")
            .setLsnrLeft(() -> setModeNewGroup(false));


    public GuiRecipe(String group, int index) {
        this();
        dest = new Pair<>(group, index);
        Recipe r = Controller.getRecipe(group, index);
        fromRecipe(r);
        switcherGroup.setIndex(Controller.getGroups().indexOf(group));
        buttonCopy.setDisabled(false);
        buttonDel.setDisabled(false);
    }

    public GuiRecipe() {
        addAll(new WHelp("recipe"), new WPanel());
        add(new WIcon(7, 63, 21, 20, Resource.ICN_OUTPUT, "recipe.output"));
        add(new WIcon(7, 87, 21, 20, Resource.ICN_CATALYST, "recipe.catalyst"));
        add(new WIcon(7, 111, 21, 40, Resource.ICN_INPUT, "recipe.input"));
        add(new WLine(57));
        addAll(groupInput, groupCatalyst, groupOutput, switcherGroup);
        if (switcherGroup.getTexts().isEmpty()) switcherGroup.setTemp(Utilities.I18n.format("common.default"));
        setModeNewGroup(false);
        buttonCopy.setDisabled(true);
        buttonDel.setDisabled(true);
        buttonDisamb.setDisabled(true);
    }

    public void setModeNewGroup(boolean b) {
        if (b) {
            removeAll(buttonNew, buttonLabel, buttonClear, buttonCopy, buttonSave, buttonDisamb, buttonDel);
            addAll(buttonYes, buttonNo, textField);
        } else {
            addAll(buttonNew, buttonLabel, buttonClear, buttonCopy, buttonSave, buttonDisamb, buttonDel);
            removeAll(buttonYes, buttonNo, textField);
        }
    }

    public void clear() {
        groupInput.setLabel(Collections.nCopies(14, ILabel.EMPTY), 0);
        groupCatalyst.setLabel(Collections.nCopies(7, ILabel.EMPTY), 0);
        groupOutput.setLabel(Collections.nCopies(7, ILabel.EMPTY), 0);
    }

    public void transfer(IRecipeHandler recipe, int recipeIndex) {
        // item disamb raw
        ArrayList<Triple<ILabel, CostList, CostList>> input = new ArrayList<>();
        ArrayList<Triple<ILabel, CostList, CostList>> output = new ArrayList<>();
        disambiguation = new HashMap<>();

        // input
        List<ItemStack> itemStacks = recipe.getIngredientStacks(recipeIndex).stream()
                                           .map((positionedStack) -> positionedStack.item).collect(Collectors.toList());
        merge(input, itemStacks.stream().map(ILabel.Converter::from).collect(Collectors.toList()));

        // output
        ItemStack outputStack = recipe.getResultStack(recipeIndex).item;
        merge(output, Stream.of(outputStack).map(ILabel.Converter::from).collect(Collectors.toList()));

        // catalyst ignore multiple catalyst
        NEIPlugin.getCatalyst(recipe).map(ILabel.Converter::from).ifPresent(catalyst -> {
            groupCatalyst.setLabel(catalyst, 0);
        });

        groupInput.setLabel(sort(input, 0, true), 0);
        groupOutput.setLabel(sort(output, 21, false), 0);
        refresh();
    }

    private void merge(ArrayList<Triple<ILabel, CostList, CostList>> dst, List<ILabel> list) {
        if (list.isEmpty())
            return;
        dst.stream().filter(p -> {
            CostList cl = new CostList(list);
            if (p.three.equals(cl)) {
                ILabel.MERGER.merge(p.one, ILabel.CONVERTER.first(list)).ifPresent(i -> p.one = i);
                p.two = p.two.merge(cl, true, false);
                return true;
            } else
                return false;
        }).findAny().orElseGet(() -> {
            Triple<ILabel, CostList, CostList> ret = new Triple<>(ILabel.CONVERTER.first(list), new CostList(list),
                                                                  new CostList(list));
            dst.add(ret);
            return ret;
        });
    }

    private ArrayList<ILabel> sort(ArrayList<Triple<ILabel, CostList, CostList>> src, int offset, boolean guess) {
        ArrayList<ILabel> ret = new ArrayList<>();
        for (int i = 0; i < src.size(); i++) {
            Triple<ILabel, CostList, CostList> p = src.get(i);
            ret.add(p.one);
            if (guess && !ILabel.CONVERTER.guess(p.three.getLabels()).isEmpty())
                disambiguation.put(i + offset, p.two.getLabels());
        }
        return ret;
    }

    private Recipe toRecipe() {
        return new Recipe(groupInput.getLabels(), groupCatalyst.getLabels(), groupOutput.getLabels());
    }

    void fromRecipe(Recipe r) {
        groupInput.setLabel(
                Arrays.stream(r.getLabel(Recipe.enumIoType.INPUT)).map(ILabel::copy).collect(Collectors.toList()), 0);
        groupCatalyst.setLabel(
                Arrays.stream(r.getLabel(Recipe.enumIoType.CATALYST)).map(ILabel::copy).collect(Collectors.toList()),
                0);
        groupOutput.setLabel(
                Arrays.stream(r.getLabel(Recipe.enumIoType.OUTPUT)).map(ILabel::copy).collect(Collectors.toList()), 0);
    }


    void refresh() {
        buttonDisamb.setDisabled(disambiguation.isEmpty());
    }
}
