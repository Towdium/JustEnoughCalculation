package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.Recipe.IO;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_RED;
import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_WHITE;
import static me.towdium.jecalculation.gui.JecaGui.FontType.PLAIN;
import static me.towdium.jecalculation.gui.Resource.*;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class GuiRecipe extends Gui {
    Pair<String, Integer> dest;
    WSwitcher group = new WSwitcher(7, 7, 162, Controller.getGroups()).setListener(i -> refresh());
    WTextField text = new WTextField(49, 25, 119);
    WLabelScroll catalyst = new WLabelScroll(25, 101, 7, 1, true).setLsnrScroll(GuiRecipe::handleLabelScroll);
    WLabelScroll input = new WLabelScroll(25, 123, 7, 2, true).setLsnrScroll(GuiRecipe::handleLabelScroll);
    WLabelScroll output = new WLabelScroll(25, 61, 7, 2, true).setLsnrScroll(GuiRecipe::handleLabelScroll);
    WButton clear = new WButtonIcon(64, 25, 20, 20, BTN_DEL, "recipe.clear").setListener(i -> reset());
    // check duplicate and valid
    WButton copy = new WButtonIcon(83, 25, 20, 20, BTN_COPY, "recipe.copy").setListener(i -> {
        Controller.addRecipe(group.getText(), toRecipe());
        JecaGui.displayParent();
    });
    WButton label = new WButtonIcon(45, 25, 20, 20, BTN_LABEL, "recipe.label").setListener(i ->
            JecaGui.displayGui(new GuiLabel((l) -> {
                JecaGui.displayParent();
                JecaGui.getCurrent().hand = l;
            })));
    WButton save = new WButtonIcon(26, 25, 20, 20, BTN_SAVE, "recipe.save").setDisabled(true).setListener(i -> {
        if (dest == null)
            Controller.addRecipe(group.getText(), toRecipe());
        else {
            String group = this.group.getText();
            if (group.equals(dest.one)) Controller.setRecipe(dest.one, dest.two, toRecipe());
            else Controller.setRecipe(group, dest.one, dest.two, toRecipe());
        }
        JecaGui.displayParent();
    });
    WButton yes = new WButtonIcon(7, 25, 20, 20, BTN_YES, "recipe.confirm").setDisabled(true).setListener(i -> {
        group.setText(text.getText());
        text.setText("");
        setNewGroup(false);
        refresh();
    });
    WButton no = new WButtonIcon(26, 25, 20, 20, BTN_NO, "common.cancel").setListener(i -> setNewGroup(false));
    WButton neu = new WButtonIcon(7, 25, 20, 20, BTN_NEW, "recipe.new").setListener(i -> setNewGroup(true));
    EnumMap<IO, Map<Integer, List<ILabel>>> disamb = new EnumMap<>(IO.class);

    public GuiRecipe(String group, int index) {
        this();
        dest = new Pair<>(group, index);
        Recipe r = Controller.getRecipe(group, index);
        for (IO i : IO.values()) {
            getWidget(i).setLabels(r.getLabel(i).stream()
                    .map(ILabel::copy).collect(Collectors.toList()));
        }
        this.group.setIndex(Controller.getGroups().indexOf(group));
        refresh();
    }

    public GuiRecipe() {
        for (IO j : IO.values()) {
            getWidget(j).setFmtAmount(i -> i.getAmountString(false))
                    .setFmtTooltip((i, k) -> i.getToolTip(k, true))
                    .setLsnrClick((i, v) -> {
                        ILabel l = i.get(v).getLabel();
                        if (l != ILabel.EMPTY) setOverlay(new WAmount(j, v));
                    })
                    .setLsnrUpdate((i, v) -> {
                        refresh();
                        removeDisamb(j, v);
                    });
        }
        add(new WHelp("recipe"), new WPanel());
        add(new WIcon(7, 61, 18, 36, ICN_OUTPUT, "common.output"));
        add(new WIcon(7, 101, 18, 18, ICN_CATALYST, "common.catalyst"));
        add(new WIcon(7, 123, 18, 36, ICN_INPUT, "common.input"));
        add(new WLine(52));
        add(catalyst, input, output, group);
        if (group.getTexts().isEmpty()) group.setText(Utilities.I18n.get("gui.common.default"));
        String last = Controller.getLast();
        int index = -1;
        if (last != null) index = group.getTexts().indexOf(last);
        if (index != -1) group.setIndex(index);
        setNewGroup(false);
        copy.setDisabled(true);
        text.setListener(i -> yes.setDisabled(i.getText().isEmpty()));
    }

    public WLabelScroll getWidget(IO type) {
        return Recipe.get(type, input, output, catalyst);
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
    public boolean acceptsTransfer() {
        return true;
    }

    @Override
    public boolean acceptsLabel() {
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
        for (IO i : IO.values()) getWidget(i).setLabels(new ArrayList<>());
        disamb.clear();
        refresh();
    }

    public void transfer(EnumMap<IO, List<Trio<ILabel, CostList, CostList>>> recipe, Class<?> context) {
        disamb.clear();

        // generate disamb and fill slots
        for (IO i : IO.values()) getWidget(i).setLabels(extract(recipe, i, context));
        refresh();
    }

    private ArrayList<ILabel> extract(EnumMap<IO, List<Trio<ILabel, CostList, CostList>>> src, IO type, Class<?> context) {
        List<Trio<ILabel, CostList, CostList>> l = src.get(type);
        ArrayList<ILabel> ret = new ArrayList<>();
        if (l == null) return ret;
        for (int i = 0; i < l.size(); i++) {
            Trio<ILabel, CostList, CostList> p = l.get(i);
            ret.add(p.one);
            if (p.two.getLabels().size() > 1) {
                List<ILabel> raw = p.two.getLabels();
                List<ILabel> suggest = new ArrayList<>();
                suggest.addAll(ILabel.CONVERTER.guess(raw, context).one);
                suggest.addAll(raw);
                disamb.computeIfAbsent(type, j -> new HashMap<>()).put(i, suggest);
            }
        }
        return ret;
    }

    private List<ILabel> trim(List<ILabel> ls) {
        for (int i = ls.size() - 1; i >= 0; i--) {
            if (ls.get(i) == ILabel.EMPTY) ls.remove(i);
        }
        return ls;
    }

    private Recipe toRecipe() {
        return new Recipe(trim(input.getLabels()), trim(catalyst.getLabels()), trim(output.getLabels()));
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

    private void removeDisamb(IO type, int index) {
        Map<Integer, List<ILabel>> entry = disamb.get(type);
        if (entry != null) entry.remove(index);
    }

    static boolean handleLabelScroll(WLabel w, int diff) {
        ILabel l = w.getLabel();
        for (int i = 0; i < Math.abs(diff); i++)
            l = diff > 0 ? l.increaseAmount() : l.decreaseAmount();
        w.setLabel(l, true);
        return true;
    }

    class WAmount extends WOverlay {
        WLabel temp;
        WButton number;
        WTextField text;
        WButton percent;
        WButton pick;
        WButton yes;
        WButton no;
        WButton disamb;
        WLabel ref;

        public WAmount(IO type, int idx) {
            ref = getWidget(type).get(idx);
            int x = ref.xPos;
            int y = ref.yPos;
            number = new WButtonText(x + 78, y - 1, 20, 20, "#", "recipe.to_percent")
                    .setListener(i -> {
                        temp.getLabel().setPercent(true);
                        update();
                    });
            percent = new WButtonText(x + 78, y - 1, 20, 20, "%", "recipe.to_amount")
                    .setListener(i -> {
                        temp.getLabel().setPercent(false);
                        update();
                    });
            temp = new WLabel(x - 1, y - 1, 20, 20, true).setLsnrUpdate((i, v) -> update());
            temp.setLabel(ref.getLabel().copy());
            add(new WPanel(x - 7, y - 30, 111, 55));
            add(new WText(x + 21, y + 5, PLAIN, "x"));
            text = new WTextField(x + 28, y + 9 - WTextField.HEIGHT / 2, 50);
            pick = new WButtonIcon(x + 21, y - 24, 20, 20, BTN_PICK, "recipe.pick")
                    .setListener(i -> {
                        JecaGui.getCurrent().hand = temp.getLabel();
                        set(ILabel.EMPTY, type, idx);
                    });
            yes = new WButtonIcon(x + 59, y - 24, 20, 20, BTN_YES, "recipe.confirm")
                    .setListener(i -> set(temp.getLabel(), type, idx));
            no = new WButtonIcon(x + 78, y - 24, 20, 20, BTN_NO, "recipe.delete")
                    .setListener(i -> set(ILabel.EMPTY, type, idx));
            disamb = new WButtonIcon(x + 40, y - 24, 20, 20, BTN_DISAMB, "recipe.disamb");
            Map<Integer, List<ILabel>> entry = GuiRecipe.this.disamb.get(type);
            if (entry != null && entry.containsKey(idx)) {
                disamb.setListener(i -> GuiRecipe.this.setOverlay(new WDisamb(type, idx)));
            } else disamb.setDisabled(true);
            add(temp, text, pick, yes, no, disamb);
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

        private void set(ILabel l, IO type, int idx) {
            ref.setLabel(l, true);
            removeDisamb(type, idx);
            GuiRecipe.this.setOverlay(null);
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

    class WDisamb extends WOverlay {
        WLabel temp;
        WLabelScroll content;
        WTextField search;

        public WDisamb(IO type, int idx) {
            WLabelScroll list = getWidget(type);
            WLabel ref = list.get(idx);
            int x = ref.xPos;
            int y = ref.yPos;
            add(new WPanel(x - 7, y - 46, 111, 71));
            temp = new WLabel(x - 1, y - 1, 20, 20, false);
            temp.setLabel(ref.getLabel().copy());
            content = new WLabelScroll(x + 8, y - 40, 4, 2, false)
                    .setLabels(disamb.get(type).get(idx))
                    .setLsnrClick((i, v) -> {
                        list.setLabel(idx, i.get(v).getLabel().copy().multiply(-1));
                        GuiRecipe.this.setOverlay(null);
                        refresh();
                    });
            add(new WIcon(x + 22, y - 1, 20, 20, ICN_TEXT, "common.search"));
            search = new WSearch(x + 42, y - 1, 56, content);
            add(temp, content, search);
        }
    }
}
