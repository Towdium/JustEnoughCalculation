package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.core.Recipe;
import me.towdium.jecalculation.gui.JECGuiContainer;
import me.towdium.jecalculation.util.Utilities;
import me.towdium.jecalculation.util.helpers.PlayerRecordHelper;
import me.towdium.jecalculation.util.wrappers.Pair;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Author:  Towdium
 * Created: 2016/6/15.
 */
public abstract class GuiList extends JECGuiContainer {
    int row;
    int top;
    int page = 1;
    int total = 0;
    int group = 0;
    List<GuiButton> buttons;
    List<Pair<String, Integer>> result;

    public GuiList(Container inventorySlotsIn, GuiScreen parent, int row, int top) {
        super(inventorySlotsIn, parent);
        this.row = row;
        this.top = top;
    }

    @Override
    protected int getSizeSlot(int index) {
        return 0;
    }

    @Override
    public void init() {
        Function<JECGuiButton, JECGuiButton> genButtonEdit = (button) -> {
            button.setLsnLeft(() -> {
                Pair<String, Integer> pair = result.get((page - 1) * row + (button.id - 4) / 2);
                Utilities.openGui(new GuiEditor(this, pair));
            });
            return button;
        };
        Function<JECGuiButton, JECGuiButton> genButtonDel = (button) -> {
            button.setLsnLeft(() -> {
                Pair<String, Integer> pair = result.get((page - 1) * row + (button.id - 5) / 2);
                PlayerRecordHelper.removeRecipe(pair.one, pair.two);
                updateLayout();
            });
            return button;
        };

        buttonList.add(new JECGuiButton(0, guiLeft + 7, guiTop + 133, 13, 12, "<", false).setLsnLeft(() -> {
            --group;
            updateLayout();
        }));
        buttonList.add(new JECGuiButton(1, guiLeft + 156, guiTop + 133, 13, 12, ">", false).setLsnLeft(() -> {
            ++group;
            updateLayout();
        }));
        buttonList.add(new JECGuiButton(2, guiLeft + 7, guiTop + 147, 13, 12, "<", false).setLsnLeft(() -> {
            --page;
            updateLayout();
        }));
        buttonList.add(new JECGuiButton(3, guiLeft + 156, guiTop + 147, 13, 12, ">", false).setLsnLeft(() -> {
            ++page;
            updateLayout();
        }));
        buttons = new ArrayList<>(row * 2);
        for (int i = 0; i < row; i++) {
            buttons.add(genButtonEdit.apply(new JECGuiButton(2 * i + 4, guiLeft + 83, guiTop + top + 20 * i, 41, 18, "edit")));
            buttons.add(genButtonDel.apply(new JECGuiButton(1 + 2 * i + 4, guiLeft + 128, guiTop + top + 20 * i, 41, 18, "delete")));
        }
        buttonList.addAll(buttons);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCenteredStringMultiLine(fontRenderer,
                PlayerRecordHelper.getSizeGroup() > group ? PlayerRecordHelper.getGroupName(group) :
                        localization("noRecord"), 7, 169, 133, 145, 0xFFFFFF);
        drawCenteredStringMultiLine(fontRenderer, page + "/" + total, 7, 169, 147, 159, 0xFFFFFF);
    }

    protected void putRecipe(int position, @Nullable Recipe recipe) {
        if (recipe == null) {
            for (int i = 0; i < 4; i++) {
                inventorySlots.getSlot(position * 4 + i).putStack(ItemStack.EMPTY);
            }
        } else {
            List<ItemStack> buffer = recipe.getOutput();
            for (int i = 0; i < 4; i++) {
                inventorySlots.getSlot(position * 4 + i).putStack(buffer.get(i));
            }
        }
    }

    @Override
    public void updateLayout() {
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
