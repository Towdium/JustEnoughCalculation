package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.gui.JECContainer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: Towdium
 * Date:   2016/7/31.
 */
public class GuiPickerOreDict extends GuiPicker {
    Consumer<ItemStack> callback;

    public GuiPickerOreDict(GuiScreen parent, List<ItemStack> stacks, Consumer<ItemStack> callback) {
        super(new JECContainer() {
            @Override
            protected void addSlots() {
                addSlotGroup(8, 34, 18, 18, 6, 9);
            }

            @Override
            public EnumSlotType getSlotType(int index) {
                return EnumSlotType.PICKER;
            }
        }, parent, 6, stacks);
        this.callback = callback;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiPickerOreDict.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        fontRendererObj.drawString(localization(GuiPicker.class, "search"), guiLeft + 7, guiTop + 13, 4210752);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    GuiTextField getSearchField(FontRenderer renderer) {
        return new GuiTextField(0, renderer, guiLeft + 52, guiTop + 8, 75, 18);
    }

    @Nullable
    @Override
    protected String getButtonTooltip(int buttonId) {
        return null;
    }

    @Override
    protected void onItemStackPick(ItemStack itemStack) {
        callback.accept(itemStack);
    }

    @Override
    protected int getSizeSlot(int index) {
        return 18;
    }
}
