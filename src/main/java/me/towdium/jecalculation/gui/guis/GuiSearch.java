package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiSearch extends WContainer implements IGui {
    List<Trio<Recipe, String, Integer>> content;
    WSwitcher group;
    WLabelScroll labels;
    WButton export;

    public GuiSearch() {
        labels = new WLabelScroll(7, 51, 8, 6, WLabel.enumMode.PICKER, true);
        labels.setLsnrUpdate(i -> content.stream()
                .filter(j -> j.one.getRep() == i)
                .findFirst()
                .ifPresent(j -> JecaGui.displayGui(true, true, new GuiRecipe(j.two, j.three))));
        add(new WPanel());
        add(new WSearch(25, 25, 90, labels));
        add(new WIcon(7, 25, 20, 20, Resource.ICN_TEXT, "common.search"));
        add(new WButtonIcon(131, 25, 20, 20, Resource.BTN_IN, "search.import")
                .setLsnrLeft(() -> JecaGui.displayGui(new GuiImport())));
        add(labels);
    }

    public void refresh() {
        int iGroup = group.getIndex();
        content = iGroup == 0 ? Controller.getRecipes() : Controller.getRecipes(group.getText());
        labels.setLabels(content.stream().map(i -> i.one.getRep()).collect(Collectors.toCollection(ArrayList::new)));
        remove(export);
        String tooltip, name;
        List<Trio<Recipe, String, Integer>> recipes;
        if (iGroup == 0) {
            tooltip = "search.export_all";
            name = null;
            recipes = Controller.getRecipes();
        } else {
            tooltip = "search.export_group";
            name = Controller.getGroups().get(iGroup - 1);
            recipes = Controller.getRecipes(name);
        }
        export = new WButtonIcon(149, 25, 20, 20, Resource.BTN_OUT, tooltip).setLsnrLeft(() -> {
            File f = iGroup == 0 ? Controller.export() : Controller.export(name);
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation(
                    "jecalculation.chat.export", f.getAbsolutePath()));
        }).setDisabled(recipes.size() == 0);
        add(export);
    }

    @Override
    public void onVisible(JecaGui gui) {
        removeAll(group);
        ArrayList<String> groups = new ArrayList<>();
        groups.add(Utilities.I18n.format("gui.search.all"));
        groups.addAll(Controller.getGroups());
        int index = group == null ? 0 : group.getIndex();
        if (index >= groups.size()) index = groups.size() - 1;
        group = new WSwitcher(7, 7, 162, groups).setListener(this::refresh);
        group.setIndex(index);
        addAll(group);
        refresh();
    }
}
