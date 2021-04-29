package me.towdium.jecalculation.client.gui.guis;

import codechicken.nei.recipe.IRecipeHandler;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;
import me.towdium.jecalculation.core.label.ILabel;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.towdium.jecalculation.client.gui.drawables.WLabel.enumMode.EDITOR;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
public class GuiRecipe extends WContainer {
    List<List<ItemStack>> disambiguation;
    WButton buttonSave = new WButtonIcon(26, 33, 20, 20, Resource.BTN_SAVE_N, Resource.BTN_SAVE_F, "recipe.save");
    WButton buttonCopy = new WButtonIcon(83, 33, 20, 20, Resource.BTN_COPY_N, Resource.BTN_COPY_F, Resource.BTN_COPY_D,
                                         "recipe.copy").setDisabled(true);
    WButton buttonDel = new WButtonIcon(64, 33, 20, 20, Resource.BTN_DEL_N, Resource.BTN_DEL_F, "recipe.clear");
    WButton buttonLabel = new WButtonIcon(45, 33, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "recipe.label")
            .setListenerLeft(() -> JecGui.displayGui(new GuiLabel((l) -> {
                JecGui.displayParent();
                JecGui.getCurrent().hand = l;
            })));
    WButton buttonDisamb = new WButtonIcon(102, 33, 20, 20, Resource.BTN_DISAMB_N,
                                           Resource.BTN_DISAMB_F, Resource.BTN_DISAMB_D, "recipe.disamb").setDisabled(true).setListenerLeft(() -> {
        if (disambiguation != null) JecGui.displayGui(new GuiDisambiguation(disambiguation).setCallback(l -> {
            JecGui.displayParent();
            JecGui.getCurrent().hand = l;
        }));
    });
    WButton buttonYes = new WButtonIcon(7, 33, 20, 20, Resource.BTN_YES_N, Resource.BTN_YES_F, "recipe.confirm");
    WButton buttonNo = new WButtonIcon(26, 33, 20, 20, Resource.BTN_NO_N, Resource.BTN_NO_F, "recipe.abort");
    WLabelGroup groupInput = new WLabelGroup(28, 111, 7, 2, 20, 20, EDITOR);
    WLabelGroup groupCatalyst = new WLabelGroup(28, 87, 7, 1, 20, 20, EDITOR);
    WLabelGroup groupOutput = new WLabelGroup(28, 63, 7, 1, 20, 20, EDITOR);
    WTextField textField = new WTextField(49, 33, 119);
    WButton buttonNew = new WButtonIcon(7, 33, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "recipe.new")
            .setListenerLeft(() -> setModeNewGroup(true));

    public GuiRecipe() {
        add(new WPanel());
        add(new WSwitcher(7, 7, 162, 1));
        add(new WIcon(7, 63, 21, 20, Resource.ICN_OUTPUT_N, Resource.ICN_OUTPUT_F, "recipe.output"));
        add(new WIcon(7, 87, 21, 20, Resource.ICN_CATALYST_N, Resource.ICN_CATALYST_F, "recipe.catalyst"));
        add(new WIcon(7, 111, 21, 40, Resource.ICN_INPUT_N, Resource.ICN_INPUT_F, "recipe.input"));
        add(new WLine(57));
        addAll(groupInput, groupCatalyst, groupOutput);
        setModeNewGroup(false);
    }

    public void setModeNewGroup(boolean b) {
        if (b) {
            removeAll(buttonNew, buttonLabel, buttonDel, buttonCopy, buttonSave, buttonDisamb);
            addAll(buttonYes, buttonNo, textField);
        } else {
            addAll(buttonNew, buttonLabel, buttonDel, buttonCopy, buttonSave, buttonDisamb);
            removeAll(buttonYes, buttonNo, textField);
        }
    }

    public void transfer(IRecipeHandler recipe, int recipeIndex) {
        ArrayList<List<ItemStack>> buf = new ArrayList<>();
        ArrayList<ILabel> input = new ArrayList<>();
        ArrayList<ILabel> output = new ArrayList<>();

        BiConsumer<ArrayList<ILabel>, ILabel> merge = (list, label) -> IntStream.range(0, list.size()).filter(i -> {
            Optional<ILabel> l = ILabel.MERGER.merge(list.get(i), label, true);
            boolean ret = l.isPresent();
            if (ret) list.set(i, l.get());
            return ret;
        }).findAny().orElseGet(() -> {
            list.add(label);
            return -1;
        });


        List<ILabel> raw = new ArrayList<>();

        // input
        List<ItemStack> itemStacks = recipe.getIngredientStacks(recipeIndex).stream()
                                           .map((positionedStack) -> positionedStack.item).collect(Collectors.toList());

        itemStacks.forEach((itemStack) -> {
            raw.add(ILabel.CONVERTER_ITEM.toLabel(itemStack));
        });
        List<ILabel> guessed = ILabel.CONVERTER_ITEM.toLabel(itemStacks);
        merge.accept(input, guessed.isEmpty() ? ILabel.CONVERTER_ITEM.toLabel(itemStacks.get(0)) : guessed.get(0));

        buf.add(itemStacks);

        // output
        ItemStack outputStack = recipe.getResultStack(recipeIndex).item;
        merge.accept(output, ILabel.CONVERTER_ITEM.toLabel(outputStack));
        buf.add(Collections.singletonList(outputStack));

        groupInput.setLabel(input, 0);
        groupOutput.setLabel(output, 0);
        disambiguation = buf;
        buttonDisamb.setDisabled(buf.isEmpty());
    }
}
