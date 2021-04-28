package me.towdium.jecalculation.client.gui.guis;

import codechicken.nei.recipe.IRecipeHandler;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;
import me.towdium.jecalculation.core.labels.ILabel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.client.gui.drawables.DLabel.enumMode.EDITOR;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
public class GuiRecipe extends DContainer {
    DButton buttonSave = new DButtonIcon(26, 33, 20, 20, Resource.BTN_SAVE_N, Resource.BTN_SAVE_F, "recipe.save");
    DButton buttonCopy = new DButtonIcon(83, 33, 20, 20, Resource.BTN_COPY_N, Resource.BTN_COPY_F, "recipe.copy");
    DButton buttonDel = new DButtonIcon(64, 33, 20, 20, Resource.BTN_DEL_N, Resource.BTN_DEL_F, "recipe.clear");
    DButton buttonLabel = new DButtonIcon(45, 33, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "recipe.label");
    DButton buttonYes = new DButtonIcon(7, 33, 20, 20, Resource.BTN_YES_N, Resource.BTN_YES_F, "recipe.confirm");
    DButton buttonNo = new DButtonIcon(26, 33, 20, 20, Resource.BTN_NO_N, Resource.BTN_NO_F, "recipe.abort");
    DLabelGroup groupInput = new DLabelGroup(28, 111, 7, 2, 20, 20, EDITOR);
    DLabelGroup groupCatalyst = new DLabelGroup(28, 87, 7, 1, 20, 20, EDITOR);
    DLabelGroup groupOutput = new DLabelGroup(28, 63, 7, 1, 20, 20, EDITOR);
    DTextField textField = new DTextField(49, 33, 119);
    DButton buttonNew = new DButtonIcon(7, 33, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "recipe.new")
            .setListenerLeft(() -> setModeNewGroup(true));

    public GuiRecipe() {
        add(new DPanel());
        add(new DSwitcher(7, 7, 162, 2));
        add(new DIcon(7, 63, 21, 20, Resource.ICN_OUTPUT_N, Resource.ICN_OUTPUT_F, "recipe.output"));
        add(new DIcon(7, 87, 21, 20, Resource.ICN_CATALYST_N, Resource.ICN_CATALYST_F, "recipe.catalyst"));
        add(new DIcon(7, 111, 21, 40, Resource.ICN_INPUT_N, Resource.ICN_INPUT_F, "recipe.input"));
        add(new DLine(57));
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
