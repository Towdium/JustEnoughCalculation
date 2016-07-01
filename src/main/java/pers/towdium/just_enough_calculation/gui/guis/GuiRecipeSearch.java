package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.gui.JECContainer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author:  Towdium
 * Created: 2016/6/15.
 */
public class GuiRecipeSearch extends GuiRecipeList {
    GuiButton buttonMode;
    GuiButton buttonSearch;
    EnumMode mode = EnumMode.OUT;

    public GuiRecipeSearch(GuiScreen parent) {
        super(new JECContainer() {
            @Override
            protected void addSlots() {
                addSlotSingle(9, 9);
            }

            @Override
            public EnumSlotType getSlotType(int index) {
                return index == 20 ? EnumSlotType.SELECT : EnumSlotType.DISABLED;
            }
        }, parent, 6, 6);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonMode = new GuiButton(12, 31 + guiLeft, 7 + guiTop, 84, 20, "mode");
        buttonSearch = new GuiButton(13, 119 + guiLeft, 7 + guiTop, 50, 20, "search");
        buttonList.add(buttonMode);
        buttonList.add(buttonSearch);
    }

    @Override
    protected List<Recipe> getSuitableRecipeIndex(List<Recipe> recipeList) {
        return null;
    }

    @Nullable
    @Override
    protected String getButtonTooltip(int buttonId) {
        return null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiRecipeSearch.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected int getSizeSlot(int index) {
        return index == 20 ? 20 : 18;
    }

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
}
