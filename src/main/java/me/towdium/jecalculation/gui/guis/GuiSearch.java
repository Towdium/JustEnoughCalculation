package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiSearch extends WContainer implements IGui {
    List<Triple<Recipe, String, Integer>> content;
    WSwitcher switcherGroup;
    WLabelScroll labelScroll;

    public GuiSearch() {
        labelScroll = new WLabelScroll(7, 51, 8, 6, WLabel.enumMode.PICKER, true);
        add(new WPanel());
        add(new WSearch(i -> content.stream().filter(j -> j.one.getRep() == i).findFirst().ifPresent(j ->
                JecaGui.displayGui(true, true, new GuiRecipe(j.two, j.three))), new WTextField(25, 27, 90), labelScroll));
        add(new WIcon(7, 27, 20, 20, Resource.ICN_TEXT, "search.text"));
    }

    public void refresh() {
        int iGroup = switcherGroup.getIndex();
        content = iGroup == 0 ? Controller.getRecipes() : Controller.getRecipes(switcherGroup.getText());
        labelScroll.setLabels(content.stream().map(i -> i.one.getRep()).collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public void onVisible(JecaGui gui) {
        remove(switcherGroup);
        ArrayList<String> groups = new ArrayList<>();
        groups.add(Utilities.I18n.format("gui.search.all"));
        groups.addAll(Controller.getGroups());
        int index = switcherGroup == null ? 0 : switcherGroup.getIndex();
        if (index >= groups.size()) index = groups.size() - 1;
        switcherGroup = new WSwitcher(7, 7, 162, groups).setListener(this::refresh);
        switcherGroup.setIndex(index);
        add(switcherGroup);
        refresh();
    }
}
