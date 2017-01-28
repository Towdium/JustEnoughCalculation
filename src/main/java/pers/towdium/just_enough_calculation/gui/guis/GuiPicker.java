package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.util.Utilities;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Author: Towdium
 * Date:   2016/7/29.
 */
public abstract class GuiPicker extends JECGuiContainer {
    GuiTextField searchField;
    List<ItemStack> stacks;
    int row;
    int page = 1;
    int total;

    public GuiPicker(JECContainer container, GuiScreen parent, int row, List<ItemStack> stacks) {
        super(container, parent);
        this.row = row;
        this.stacks = stacks;
    }

    @Override
    public void init() {
        buttonList.add(new JECGuiButton(0, guiLeft + 7, guiTop + 147, 13, 12, "<", false).setLsnLeft(() -> {
            page = total == 0 ? 0 : page == total ? 1 : page + 1;
            updateLayout();
        }));
        buttonList.add(new JECGuiButton(1, guiLeft + 156, guiTop + 147, 13, 12, ">", false).setLsnLeft(() -> {
            page = total == 0 ? 0 : page == 1 ? total : page - 1;
            updateLayout();
        }));
        searchField = getSearchField(fontRendererObj);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        searchField.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCenteredStringMultiLine(fontRendererObj, page + "/" + total, 7, 169, 147, 159, 0xFFFFFF);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!searchField.textboxKeyTyped(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        } else {
            updateLayout();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateLayout() {
        List<ItemStack> buffer = new ArrayList<>();
        String text = searchField.getText().toLowerCase();
        if (text.length() != 0 && text.charAt(0) == '@') {
            String textAlt = text.substring(1);
            stacks.forEach(itemStack -> {
                String name = Utilities.getModName(itemStack);
                if (name != null && name.toLowerCase().contains(textAlt)) buffer.add(itemStack);
            });
        } else {
            stacks.forEach(itemStack -> {
                if (itemStack.getDisplayName().toLowerCase().contains(text)) buffer.add(itemStack);
            });
        }
        searchField.setTextColor(buffer.size() == 0 ? 0xFF0000 : 0xFFFFFF);
        total = (buffer.size() + (9 * row) - 1) / (9 * row);
        page = page > total ? total : page == 0 && total != 0 ? 1 : page;
        putStacks(0, row * 9 - 1, buffer, page != 0 ? row * 9 * (page - 1) : 0);
    }

    @Override
    protected BiFunction<Long, ItemStackHelper.EnumStackAmountType, String> getFormer(int id) {
        return (aLong, type) -> "";
    }

    abstract GuiTextField getSearchField(FontRenderer renderer);
}
