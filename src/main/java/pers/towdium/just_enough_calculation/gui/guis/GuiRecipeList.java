package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.util.LocalizationHelper;
import pers.towdium.just_enough_calculation.util.PlayerRecordHelper;
import pers.towdium.just_enough_calculation.util.wrappers.Pair;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:  Towdium
 * Created: 2016/6/15.
 */
public abstract class GuiRecipeList extends JECGuiContainer {
    int row;
    int top;
    int page = 1;
    int total = 0;
    int group = 0;
    List<GuiButton> buttons;
    List<Pair<String, Integer>> result;

    public GuiRecipeList(Container inventorySlotsIn, GuiScreen parent, int row, int top) {
        super(inventorySlotsIn, parent);
        this.row = row;
        this.top = top;
    }

    @Override
    protected int getSizeSlot(int index) {
        return 0;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButtonExt(0, guiLeft + 7, guiTop + 133, 13, 12, "<"));
        buttonList.add(new GuiButtonExt(1, guiLeft + 156, guiTop + 133, 13, 12, ">"));
        buttonList.add(new GuiButtonExt(2, guiLeft + 7, guiTop + 147, 13, 12, "<"));
        buttonList.add(new GuiButtonExt(3, guiLeft + 156, guiTop + 147, 13, 12, ">"));
        buttons = new ArrayList<>(row * 2);
        for (int i = 0; i < row; i++) {
            buttons.add(new GuiButtonExt(2 * i + 4, guiLeft + 83, guiTop + top + 20 * i, 41, 18, "edit"));
            buttons.add(new GuiButtonExt(1 + 2 * i + 4, guiLeft + 128, guiTop + top + 20 * i, 41, 18, "delete"));
        }
        buttonList.addAll(buttons);
        updateLayout();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCenteredStringMultiLine(fontRendererObj,
                PlayerRecordHelper.getSizeGroup() > group ? PlayerRecordHelper.getGroupName(group) :
                        LocalizationHelper.format("gui.editor.noRecord"), 7, 169, 133, 145, 0xFFFFFF);
        drawCenteredStringMultiLine(fontRendererObj, page + "/" + total, 7, 169, 147, 159, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                --group;
                updateLayout();
                break;
            case 1:
                ++group;
                updateLayout();
                break;
            case 2:
                --page;
                updateLayout();
                break;
            case 3:
                ++page;
                updateLayout();
                break;
        }
        if (button.id <= row * 2 + 3 && button.id > 3) {
            if (button.id % 2 != 0) {
                Pair<String, Integer> pair = result.get((page - 1) * row + (button.id - 4) / 2);
                PlayerRecordHelper.removeRecipe(pair.one, pair.two);
                updateLayout();
            }
        }
    }

    protected void putRecipe(int position, @Nullable Recipe recipe) {
        if (recipe == null) {
            for (int i = 0; i < 4; i++) {
                inventorySlots.getSlot(position * 4 + i).putStack(null);
            }
        } else {
            List<ItemStack> buffer = recipe.getOutput();
            for (int i = 0; i < 4; i++) {
                inventorySlots.getSlot(position * 4 + i).putStack(buffer.get(i));
            }
        }
    }

    @Override
    protected void updateLayout() {
        if (group > PlayerRecordHelper.getSizeGroup() - 1) {
            group = 0;
        } else if (group < 0) {
            group = PlayerRecordHelper.getSizeGroup() - 1;
            group = group < 0 ? 0 : group;
        }
        if (PlayerRecordHelper.getSizeGroup() > 0) {
            List<Pair<String, Integer>> buffer = new ArrayList<>();
            String name = PlayerRecordHelper.getGroupName(group);
            for (int i = PlayerRecordHelper.getRecipeInGroup(name).size() - 1; i >= 0; i--) {
                buffer.add(new Pair<>(name, i));
            }
            result = getSuitableRecipeIndex(buffer);
            total = (result.size() + row - 1) / row;
            if (page > total) {
                page = 1;
            } else if (page <= 0) {
                page = total;
            }
            for (int i = (page - 1) * row; i < page * row && page != 0; i++) {
                if (i < result.size()) {
                    putRecipe(i - (page - 1) * row, PlayerRecordHelper.getRecipe(result.get(i).one, result.get(i).two));
                    buttons.get((i - (page - 1) * row) * 2).enabled = true;
                    buttons.get((i - (page - 1) * row) * 2 + 1).enabled = true;
                } else {
                    putRecipe(i - (page - 1) * row, null);
                    buttons.get((i - (page - 1) * row) * 2).enabled = false;
                    buttons.get((i - (page - 1) * row) * 2 + 1).enabled = false;
                }
            }

        } else {
            page = 0;
            total = 0;
            for (int i = 0; i < row; i++) {
                putRecipe(i, null);
            }
            buttons.forEach(guiButton -> guiButton.enabled = false);
        }
    }

    protected abstract List<Pair<String, Integer>> getSuitableRecipeIndex(List<Pair<String, Integer>> recipeList);
}
