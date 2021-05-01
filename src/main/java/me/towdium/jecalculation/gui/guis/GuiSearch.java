package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.Recipes;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.polyfill.google.common.collect.Streams;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentTranslation;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.data.Controller.recipeIterator;
import static me.towdium.jecalculation.gui.Resource.*;
import static me.towdium.jecalculation.gui.widgets.WLabel.Mode.PICKER;

@ParametersAreNonnullByDefault
public class GuiSearch extends WContainer implements IGui {
    IdentityHashMap<ILabel, Trio<Recipe, String, Integer>> recipes;
    WLabelScroll labels = new WLabelScroll(7, 51, 8, 6, PICKER, true).setListener((i, v) -> {
        Trio<Recipe, String, Integer> recipe = recipes.get(i.get(v));
        JecaGui.displayGui(true, true, new GuiRecipe(recipe.two, recipe.three));
    });
    WSwitcher group;
    WTextField text = new WTextField(7, 25, 119);
    WButton no = new WButtonIcon(130, 25, 20, 20, BTN_NO, "common.cancel")
            .setListener(i -> setRename(false));
    WButton yes = new WButtonIcon(149, 25, 20, 20, BTN_YES, "search.confirm")
            .setDisabled(true).setListener(i -> {
                Controller.renameGroup(group.getText(), text.getText());
                refreshGroups();
                group.setText(text.getText());
                refreshDisplay();
                setRename(false);
            });
    WButton exportN = new WButtonIcon(130, 25, 20, 20, BTN_EXPORT_N, "search.export_all")
            .setListener(i -> Controller.export());
    WButton export1 = new WButtonIcon(130, 25, 20, 20, BTN_EXPORT_1, "search.export_group")
            .setListener(i -> Controller.export(group.getText()));
    WButton inport = new WButtonIcon(112, 25, 20, 20, BTN_IMPORT, "search.import")
            .setListener(i -> JecaGui.displayGui(new GuiImport()));
    WButton rename = new WButtonIcon(149, 25, 20, 20, BTN_EDIT, "search.rename")
            .setListener(i -> setRename(true));
    WIcon icon = new WIcon(7, 25, 20, 20, ICN_TEXT, "common.search");
    WSearch search = new WSearch(26, 25, 80, labels);
    List<ILabel> identifiers;

    public GuiSearch() {
        text.setListener(i -> {
            String s = i.getText();
            yes.setDisabled(s.isEmpty() || Controller.getGroups().contains(s));
        });
        add(new WHelp("search"), new WPanel());
        add(labels, icon, search, inport, exportN, rename);
    }


    private void generate() {
        identifiers = new ArrayList<>();
        recipes = new IdentityHashMap<>();
        Consumer<Trio<Recipe, String, Integer>> add = i -> {
            ILabel id = i.one.getRep();
            recipes.put(id, i);
            identifiers.add(id);
        };

        Recipes.RecipeIterator it = group.getIndex() == 0 ? recipeIterator() : recipeIterator(group.getText());
        it.stream().map(j -> new Trio<>(j, it.getGroup(), it.getIndex())).forEach(add);
    }

    @Override
    public boolean onKey(JecaGui gui, char ch, int code) {
        if (code == Keyboard.KEY_ESCAPE && contains(text)) {
            setRename(false);
            return true;
        }
        return super.onKey(gui, ch, code);
    }

    public void refreshDisplay() {
        remove(exportN, export1);
        generate();
        labels.setLabels(identifiers);
        add(group.getIndex() == 0 ? exportN : export1);
        exportN.setDisabled(identifiers.size() == 0);
        rename.setDisabled(group.getIndex() == 0);
    }

    public void refreshGroups() {
        remove(group);
        ArrayList<String> groups = new ArrayList<>();
        groups.add(Utilities.I18n.get("gui.search.all"));
        groups.addAll(Controller.getGroups());
        int index = group == null ? 0 : group.getIndex();
        if (index >= groups.size()) index = groups.size() - 1;
        group = new WSwitcher(7, 7, 162, groups).setListener(i -> refreshDisplay());
        group.setIndex(index);
        add(group);
    }

    @Override
    public void onVisible(JecaGui gui) {
        refreshGroups();
        refreshDisplay();
    }

    private void setRename(boolean b) {
        group.setDisabled(b);
        if (b) {
            add(yes, no, text);
            remove(icon, search, inport, export1, exportN, rename);
        } else {
            add(icon, search, inport, group.getIndex() == 0 ? exportN : export1, rename);
            remove(yes, no, text);
            text.setText("");
            yes.setDisabled(true);
        }
    }
}
