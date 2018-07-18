package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.algorithm.CostList;
import me.towdium.jecalculation.data.ControllerClient;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.gui.JecGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.drawables.*;
import me.towdium.jecalculation.utils.wrappers.Triple;
import mezz.jei.api.gui.IRecipeLayout;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
public class GuiRecipe extends WContainer {
    HashMap<Integer, List<ILabel>> disambiguation = new HashMap<>();
    WSwitcher switcherGroup = new WSwitcher(7, 7, 162, ControllerClient.getGroups());
    WButton buttonCopy = new WButtonIcon(83, 33, 20, 20, Resource.BTN_COPY_N, Resource.BTN_COPY_F,
            Resource.BTN_COPY_D, "recipe.copy").setDisabled(true);
    WLabelGroup groupCatalyst = new WLabelGroup(28, 87, 7, 1, 20, 20, WLabel.enumMode.EDITOR);
    WButton buttonDisamb = new WButtonIcon(102, 33, 20, 20, Resource.BTN_DISAMB_N,
            Resource.BTN_DISAMB_F, Resource.BTN_DISAMB_D, "recipe.disamb").setDisabled(true).setListenerLeft(() -> {
        if (disambiguation != null) JecGui.displayGui(new GuiDisambiguation(new ArrayList<>(disambiguation.values()))
                .setCallback(l -> {
                    JecGui.displayParent();
                    JecGui.getCurrent().hand = l;
                }));
    });
    WLabelGroup groupInput = new WLabelGroup(28, 111, 7, 2, 20, 20, WLabel.enumMode.EDITOR).setLsnrUpdate(i -> {
        disambiguation.remove(i);
        refresh();
    });
    WLabelGroup groupOutput = new WLabelGroup(28, 63, 7, 1, 20, 20, WLabel.enumMode.EDITOR).setLsnrUpdate(i -> {
        disambiguation.remove(i + 14);
        refresh();
    });
    WButton buttonClear = new WButtonIcon(64, 33, 20, 20, Resource.BTN_DEL_N, Resource.BTN_DEL_F, "recipe.clear")
            .setListenerLeft(this::clear);
    WButton buttonLabel = new WButtonIcon(45, 33, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "recipe.label")
            .setListenerLeft(() -> JecGui.displayGui(new GuiLabel((l) -> {
                JecGui.displayParent();
                JecGui.getCurrent().hand = l;
            })));
    WButton buttonSave = new WButtonIcon(26, 33, 20, 20, Resource.BTN_SAVE_N, Resource.BTN_SAVE_F, "recipe.save")
            .setListenerLeft(() -> {
                ControllerClient.addRecipe(switcherGroup.getText(), toRecipe());
                JecGui.displayParent();
            });
    WTextField textField = new WTextField(49, 33, 119);
    WButton buttonNew = new WButtonIcon(7, 33, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "recipe.new")
            .setListenerLeft(() -> setModeNewGroup(true));
    WButton buttonYes = new WButtonIcon(7, 33, 20, 20, Resource.BTN_YES_N, Resource.BTN_YES_F, "recipe.confirm")
            .setListenerLeft(() -> {
                switcherGroup.setTemp(textField.getText());
                textField.setText("");
                setModeNewGroup(false);
            });
    WButton buttonNo = new WButtonIcon(26, 33, 20, 20, Resource.BTN_NO_N, Resource.BTN_NO_F, "recipe.cancel")
            .setListenerLeft(() -> setModeNewGroup(false));

    public GuiRecipe() {
        add(new WPanel());
        add(new WIcon(7, 63, 21, 20, Resource.ICN_OUTPUT_N, Resource.ICN_OUTPUT_F, "recipe.output"));
        add(new WIcon(7, 87, 21, 20, Resource.ICN_CATALYST_N, Resource.ICN_CATALYST_F, "recipe.catalyst"));
        add(new WIcon(7, 111, 21, 40, Resource.ICN_INPUT_N, Resource.ICN_INPUT_F, "recipe.input"));
        add(new WLine(57));
        addAll(groupInput, groupCatalyst, groupOutput, switcherGroup);
        setModeNewGroup(false);
    }

    public void setModeNewGroup(boolean b) {
        if (b) {
            removeAll(buttonNew, buttonLabel, buttonClear, buttonCopy, buttonSave, buttonDisamb);
            addAll(buttonYes, buttonNo, textField);
        } else {
            addAll(buttonNew, buttonLabel, buttonClear, buttonCopy, buttonSave, buttonDisamb);
            removeAll(buttonYes, buttonNo, textField);
        }
    }

    public void clear() {
        groupInput.setLabel(Collections.nCopies(14, ILabel.EMPTY), 0);
        groupCatalyst.setLabel(Collections.nCopies(7, ILabel.EMPTY), 0);
        groupOutput.setLabel(Collections.nCopies(7, ILabel.EMPTY), 0);
    }

    public void transfer(IRecipeLayout recipe) {
        // item disamb raw
        ArrayList<Triple<ILabel, CostList, CostList>> input = new ArrayList<>();
        ArrayList<Triple<ILabel, CostList, CostList>> output = new ArrayList<>();
        HashMap<Integer, List<ILabel>> disamb = new HashMap<>();

        BiConsumer<ArrayList<Triple<ILabel, CostList, CostList>>, List<ILabel>> merge = (dst, list) -> {
            if (list.isEmpty()) return;
            dst.stream().filter(p -> {
                CostList cl = new CostList(list);
                if (p.three.equals(cl)) {
                    ILabel.MERGER.merge(p.one, ILabel.CONVERTER.first(list), true).ifPresent(i -> p.one = i.one);
                    p.two.merge(cl, true);
                    return true;
                } else return false;
            }).findAny().orElseGet(() -> {
                Triple<ILabel, CostList, CostList> ret = new Triple<>(
                        ILabel.CONVERTER.first(list), new CostList(list), new CostList(list));
                dst.add(ret);
                return ret;
            });
        };

        BiFunction<ArrayList<Triple<ILabel, CostList, CostList>>, Integer, ArrayList<ILabel>> sort = (src, offset) -> {
            ArrayList<ILabel> ret = new ArrayList<>();
            for (int i = 0; i < src.size(); i++) {
                Triple<ILabel, CostList, CostList> p = src.get(i);
                ret.add(p.one);
                if (!ILabel.CONVERTER.guess(p.three.getLabels()).isEmpty()) disamb.put(i + offset, p.two.getLabels());
            }
            return ret;
        };

        Arrays.asList(recipe.getFluidStacks(), recipe.getItemStacks()).forEach(ing ->
                ing.getGuiIngredients().forEach((i, g) -> merge.accept(g.isInput() ? input : output,
                        g.getAllIngredients().stream().map(ILabel.Converter::from).collect(Collectors.toList()))));


        groupInput.setLabel(sort.apply(input, 0), 0);
        groupOutput.setLabel(sort.apply(output, 14), 0);
        disambiguation = disamb;
        refresh();
    }

    Recipe toRecipe() {
        return new Recipe(groupInput.getLabels(), groupCatalyst.getLabels(), groupOutput.getLabels());
    }

    void refresh() {
        buttonDisamb.setDisabled(disambiguation.isEmpty());
    }
}
