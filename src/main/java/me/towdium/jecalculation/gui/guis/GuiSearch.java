package me.towdium.jecalculation.gui.guis;

import com.google.common.collect.Streams;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static me.towdium.jecalculation.gui.Resource.*;
import static me.towdium.jecalculation.gui.widgets.WLabel.Mode.PICKER;

public class GuiSearch extends WContainer implements IGui {
    WSwitcher group;
    WButton export;
    List<ILabel> identifiers;
    IdentityHashMap<ILabel, Trio<Recipe, String, Integer>> recipes;
    WLabelScroll labels = new WLabelScroll(7, 51, 8, 6, PICKER, true).setListener((i, v) -> {
        Trio<Recipe, String, Integer> recipe = recipes.get(i.get(v));
        JecaGui.displayGui(true, true, new GuiRecipe(recipe.two, recipe.three));
    });

    public GuiSearch() {
        add(new WHelp("search"), new WPanel());
        add(new WSearch(26, 25, 90, labels));
        add(new WIcon(7, 25, 20, 20, ICN_TEXT, "common.search"));
        add(new WButtonIcon(131, 25, 20, 20, BTN_IMPORT, "search.import")
                .setListener(i -> JecaGui.displayGui(new GuiImport())));
        add(labels);
    }

    private void generate() {
        identifiers = new ArrayList<>();
        recipes = new IdentityHashMap<>();
        Consumer<Trio<Recipe, String, Integer>> add = i -> {
            ILabel id = i.one.getRep();
            recipes.put(id, i);
            identifiers.add(id);
        };

        if (group.getIndex() == 0) {
            Controller.stream()
                    .flatMap(i -> Streams.mapWithIndex(i.two.stream(), (j, k) -> new Trio<>(j, i.one, (int) k)))
                    .forEach(add);
        } else {
            String s = group.getText();
            Streams.mapWithIndex(Controller.getRecipes(s).stream(), (j, k) -> new Trio<>(j, s, (int) k))
                    .forEach(add);
        }
    }

    public void refresh() {
        remove(export);
        String tooltip;
        Supplier<File> func;
        ResourceGroup texture;
        generate();
        if (group.getIndex() == 0) {
            tooltip = "search.export_all";
            func = Controller::export;
            texture = Resource.BTN_EXPORT_N;
        } else {
            tooltip = "search.export_group";
            func = () -> Controller.export(group.getText());
            texture = Resource.BTN_EXPORT_1;
        }
        labels.setLabels(identifiers);
        export = new WButtonIcon(149, 25, 20, 20, texture, tooltip).setListener(i -> {
            File f = func.get();
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation(
                    "jecalculation.chat.export", f.getAbsolutePath()));
        }).setDisabled(recipes.size() == 0);
        add(export);
    }

    @Override
    public void onVisible(JecaGui gui) {
        this.remove(group);
        ArrayList<String> groups = new ArrayList<>();
        groups.add(Utilities.I18n.get("gui.search.all"));
        groups.addAll(Controller.getGroups());
        int index = group == null ? 0 : group.getIndex();
        if (index >= groups.size()) index = groups.size() - 1;
        group = new WSwitcher(7, 7, 162, groups).setListener(i -> refresh());
        group.setIndex(index);
        this.add(group);
        refresh();
    }
}
