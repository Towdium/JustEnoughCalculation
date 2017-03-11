package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.item.ItemLabel;
import pers.towdium.just_enough_calculation.util.helpers.PlayerRecordHelper;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   08/03/17.
 */
public class GuiPickerLabelNew extends GuiPicker {
    Consumer<ItemStack> callback;
    GuiTextField fieldName;
    JECGuiButton buttonCreate;
    ItemStack stack;

    public GuiPickerLabelNew(JECGuiContainer parent, Consumer<ItemStack> callback, ItemStack stack) {
        super(new JECContainer() {
            @Override
            protected void addSlots() {
                addSlotGroup(8, 70, 18, 18, 4, 9);
            }

            @Override
            public EnumSlotType getSlotType(int index) {
                return EnumSlotType.PICKER;
            }
        }, parent, 4, PlayerRecordHelper.getListLabel());
        this.callback = callback;
        this.stack = stack;
    }

    @Override
    public void init() {
        super.init();
        fieldName = new GuiTextField(0, fontRenderer, guiLeft + 7, guiTop + 8, 108, 18);
        buttonCreate = new JECGuiButton(2, guiLeft + 119, guiTop + 7, 50, 20, "create").setLsnLeft(() -> {
            if (fieldName.getText().isEmpty()) {
                fieldName.setTextColor(0xFF0000);
                fieldName.setFocused(true);
            } else {
                callback.accept(ItemLabel.setName(stack, fieldName.getText()));
            }
        });
        buttonCreate.enabled = false;
        buttonList.add(buttonCreate);
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        boolean b = fieldName.getText().isEmpty();
        fieldName.setTextColor(b ? 0xFF0000 : 0xFFFFFF);
        buttonCreate.enabled = !b;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!fieldName.textboxKeyTyped(typedChar, keyCode))
            super.keyTyped(typedChar, keyCode);
        else
            updateLayout();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        fieldName.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    GuiTextField getSearchField(FontRenderer renderer) {
        String textSearch = localization("search");
        return new GuiTextField(0, renderer, guiLeft + fontRenderer.getStringWidth(textSearch) + 15,
                guiTop + 46, 75, 18);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID,
                "textures/gui/gui_picker_label_new.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        fontRenderer.drawString(localization("search"), guiLeft + 7, guiTop + 51, 4210752);
        fieldName.drawTextBox();
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    protected int getSizeSlot(int index) {
        return 18;
    }

    @Override
    protected void onItemStackPick(ItemStack itemStack) {
        super.onItemStackPick(itemStack);
        String name = ItemLabel.getName(itemStack);
        if (name == null) {
            throw new NullPointerException();
        } else {
            callback.accept(ItemLabel.setName(stack, name));
        }
    }
}
