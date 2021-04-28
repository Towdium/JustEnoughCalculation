package me.towdium.jecalculation.client.gui.guis;

import codechicken.nei.recipe.IRecipeHandler;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;
import me.towdium.jecalculation.core.labels.ILabel;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.client.gui.drawables.WLabel.enumMode.EDITOR;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
public class GuiRecipe extends WContainer {
    WButton buttonSave = new WButtonIcon(26, 33, 20, 20, Resource.BTN_SAVE_N, Resource.BTN_SAVE_F, "recipe.save");
    WButton buttonCopy = new WButtonIcon(83, 33, 20, 20, Resource.BTN_COPY_N, Resource.BTN_COPY_F, "recipe.copy");
    WButton buttonDel = new WButtonIcon(64, 33, 20, 20, Resource.BTN_DEL_N, Resource.BTN_DEL_F, "recipe.clear");
    WButton buttonLabel = new WButtonIcon(45, 33, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "recipe.label");
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
        add(new WSwitcher(7, 7, 162, 2));
        add(new WIcon(7, 63, 21, 20, Resource.ICN_OUTPUT_N, Resource.ICN_OUTPUT_F, "recipe.output"));
        add(new WIcon(7, 87, 21, 20, Resource.ICN_CATALYST_N, Resource.ICN_CATALYST_F, "recipe.catalyst"));
        add(new WIcon(7, 111, 21, 40, Resource.ICN_INPUT_N, Resource.ICN_INPUT_F, "recipe.input"));
        add(new WLine(57));
        addAll(groupInput, groupCatalyst, groupOutput);
        setModeNewGroup(false);
    }

    public void setModeNewGroup(boolean b) {
        if (b) {
            removeAll(buttonNew, buttonLabel, buttonDel, buttonCopy, buttonSave);
            addAll(buttonYes, buttonNo, textField);
        } else {
            addAll(buttonNew, buttonLabel, buttonDel, buttonSave); // TODO buttonCopy
            removeAll(buttonYes, buttonNo, textField);
        }
    }

    public void transfer(IRecipeHandler recipe, int recipeIndex) {
        List<List<ILabel>> buf = new ArrayList<>();
        List<ILabel> input = new ArrayList<>();
        List<ILabel> output = new ArrayList<>();

        List<ILabel> raw = new ArrayList<>();
        List<ItemStack> itemStacks = recipe.getIngredientStacks(recipeIndex).stream()
                                           .map((positionedStack) -> positionedStack.item).collect(Collectors.toList());

        itemStacks.forEach((itemStack) -> {
            raw.add(ILabel.CONVERTER_ITEM.toLabel(itemStack));
        });
        List<ILabel> guessed = ILabel.CONVERTER_ITEM.toLabel(itemStacks);
        input.add(guessed.isEmpty() ? ILabel.CONVERTER_ITEM.toLabel(itemStacks.get(0)) : guessed.get(0));
        buf.add(raw);

        ItemStack outputStack = recipe.getResultStack(recipeIndex).item;
        output.add(ILabel.CONVERTER_ITEM.toLabel(outputStack));

        groupInput.setLabel(input, 0);
        groupOutput.setLabel(output, 0);
    }
}
