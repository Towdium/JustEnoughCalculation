package me.towdium.jecalculation.gui.guis;

import com.google.common.collect.Streams;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.gui.Resource.*;
import static me.towdium.jecalculation.gui.widgets.WLabel.Mode.PICKER;

public class GuiSearch extends WContainer implements IGui {
    WSwitcher group;
    WButton export;
    List<Trio<Recipe, String, Integer>> recipes;
    WLabelScroll labels = new WLabelScroll(7, 51, 8, 6, PICKER, true).setListener((i, v) ->
            JecaGui.displayGui(true, true, new GuiRecipe(recipes.get(v).two, recipes.get(v).three)));


    public GuiSearch() {
        add(new WPanel());
        add(new WSearch(25, 25, 90, labels));
        add(new WIcon(7, 25, 20, 20, ICN_TEXT, "common.search"));
        add(new WButtonIcon(131, 25, 20, 20, BTN_IMPORT, "search.import")
                .setListener(i -> JecaGui.displayGui(new GuiImport())));
        add(labels);
    }

    private void generate() {
        if (group.getIndex() == 0) {
            recipes = Controller.stream()
                    .flatMap(i -> Streams.mapWithIndex(i.two.stream(), (j, k) -> new Trio<>(j, i.one, (int) k)))
                    .collect(Collectors.toList());
        } else {
            String s = group.getText();
            recipes = Streams.mapWithIndex(Controller.getRecipes(s).stream(), (i, j) -> new Trio<>(i, s, (int) j))
                    .collect(Collectors.toList());
        }
    }

    public void refresh() {
        remove(export);
        String tooltip;
        Supplier<File> func;
        generate();
        if (group.getIndex() == 0) {
            tooltip = "search.export_all";
            func = Controller::export;
        } else {
            tooltip = "search.export_group";
            func = () -> Controller.export(group.getText());
        }
        labels.setLabels(recipes.stream().map(i -> i.one.getRep()).collect(Collectors.toList()));
        export = new WButtonIcon(149, 25, 20, 20, BTN_EXPORT, tooltip).setListener(i -> {
            File f = func.get();
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation(
                    "jecalculation.chat.export", f.getAbsolutePath()));
        }).setDisabled(recipes.size() == 0);
        add(export);
    }

    @Override
    public void onVisible(JecaGui gui) {
        removeAll(group);
        ArrayList<String> groups = new ArrayList<>();
        groups.add(Utilities.I18n.get("gui.search.all"));
        groups.addAll(Controller.getGroups());
        int index = group == null ? 0 : group.getIndex();
        if (index >= groups.size()) index = groups.size() - 1;
        group = new WSwitcher(7, 7, 162, groups).setListener(i -> refresh());
        group.setIndex(index);
        addAll(group);
        refresh();
    }
}
