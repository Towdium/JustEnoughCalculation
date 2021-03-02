package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiRecipeSearch extends GuiRecipeList {
    GuiButton buttonMode;
    GuiButton buttonSearch;
    EnumMode mode = EnumMode.OUT;

    enum EnumMode {
        IN, OUT, IO;

        EnumMode next() {
            switch (this) {
                case OUT:
                    return IN;
                case IN:
                    return IO;
                case IO:
                    return OUT;
                default:
                    return OUT;
            }
        }

        EnumMode last() {
            switch (this) {
                case OUT:
                    return IO;
                case IN:
                    return OUT;
                case IO:
                    return IN;
                default:
                    return OUT;
            }
        }
    }

    public GuiRecipeSearch(GuiScreen parent) {
        super(new ContainerRecipeList() {
            @Override
            protected void addSlots() {
                super.addSlots();
                addSlotSingle(9, 9);
            }

            @Override
            public EnumSlotType getSlotType(int index) {
                return index == 20 ? EnumSlotType.SELECT : EnumSlotType.DISABLED;
            }
        }, parent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        buttonMode = new GuiButton(12, 31 + guiLeft, 7 + guiTop, 84, 20, "mode");
        buttonSearch = new GuiButton(13, 119 + guiLeft, 7 + guiTop, 50, 20, "search");
        buttonList.add(buttonMode);
        buttonList.add(buttonSearch);
    }

    @Override
    protected String getButtonTooltip(int buttonId) {
        return null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1) {
        this.mc.getTextureManager().bindTexture(
                new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiRecipeSearch.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected int getSizeSlot(int index) {
        return index == 20 ? 20 : 0;
    }
}
