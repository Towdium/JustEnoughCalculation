package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButtonExt(0, guiLeft + 7, guiTop + 147, 13, 12, "<"));
        buttonList.add(new GuiButtonExt(1, guiLeft + 156, guiTop + 147, 13, 12, ">"));
        searchField = getSearchField(fontRendererObj);
        updateLayout();
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
    protected void updateLayout() {
        List<ItemStack> buffer = new ArrayList<>();
        stacks.forEach(itemStack -> {
            if (itemStack.getDisplayName().toLowerCase().contains(searchField.getText())) buffer.add(itemStack);
        });
        searchField.setTextColor(buffer.size() == 0 ? 0xFF0000 : 0xFFFFFF);
        total = (buffer.size() + (9 * row) - 1) / (9 * row);
        page = page > total ? total : page == 0 && total != 0 ? 1 : page;
        putStacks(0, row * 9 - 1, buffer, page != 0 ? row * 9 * (page - 1) : 0);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0)
            page = total == 0 ? 0 : page == total ? 1 : page + 1;
        else
            page = total == 0 ? 0 : page == 1 ? total : page - 1;
        updateLayout();
    }

    abstract GuiTextField getSearchField(FontRenderer renderer);
}
