package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.util.PlayerRecordHelper;

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
        for (int i = 0; i < row; i++) {
            buttonList.add(new GuiButtonExt(2 * i + 4, guiLeft + 83, guiTop + top + 20 * i, 41, 18, "edit"));
            buttonList.add(new GuiButtonExt(1 + 2 * i + 4, guiLeft + 128, guiTop + top + 20 * i, 41, 18, "delete"));
        }
        updateLayout();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCenteredStringMultiLine(fontRendererObj, PlayerRecordHelper.getGroupName(group), 7, 169, 133, 145, 0xFFFFFF);
        drawCenteredStringMultiLine(fontRendererObj, page + "/" + total, 7, 169, 147, 159, 0xFFFFFF);

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 2:
                --page;
                updateLayout();
                break;
            case 3:
                ++page;
                updateLayout();
                break;
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

    protected void updateLayout() {
        List<Integer> bufferA = getRecipeIndex();
        List<Recipe> bufferB = new ArrayList<>();
        total = (bufferA.size() + row - 1) / row;
        page = bufferA.size() == 0 ? 0 : page == 0 ? total : page > total ? 1 : page;
        bufferA.forEach(integer -> {
            Recipe r = PlayerRecordHelper.getRecipe(integer);
            if (r.getGroup().equals(PlayerRecordHelper.getGroupName(group)))
                bufferB.add(r);
        });
        for (int i = (page - 1) * row; i < page * row && page != 0; i++) {
            if (i < bufferB.size()) {
                putRecipe(i - (page - 1) * row, bufferB.get(i));
            } else {
                putRecipe(i - (page - 1) * row, null);
            }
        }
    }

    abstract List<Integer> getRecipeIndex();
}
