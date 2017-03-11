package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.util.helpers.PlayerRecordHelper;

import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   09/03/17.
 */
public class GuiPickerLabelExisting extends GuiPicker {
    Consumer<ItemStack> callback;

    public GuiPickerLabelExisting(GuiScreen parent, Consumer<ItemStack> callback) {
        super(new JECContainer() {
            @Override
            protected void addSlots() {
                addSlotGroup(8, 34, 18, 18, 6, 9);
            }

            @Override
            public EnumSlotType getSlotType(int index) {
                return EnumSlotType.PICKER;
            }
        }, parent, 6, PlayerRecordHelper.getListLabel());
        this.callback = callback;
    }

    @Override
    GuiTextField getSearchField(FontRenderer renderer) {
        String textSearch = localization("search");
        return new GuiTextField(0, renderer, guiLeft + fontRenderer.getStringWidth(textSearch) + 15, guiTop + 8, 75, 18);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID,
                "textures/gui/gui_picker_label_existing.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        fontRenderer.drawString(localization("search"), guiLeft + 7, guiTop + 13, 4210752);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
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
