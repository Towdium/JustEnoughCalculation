package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.util.wrappers.Pair;

import java.util.List;

/**
 * Author: Towdium
 * Date:   2016/6/29.
 */
public class GuiListViewer extends GuiList {

    public GuiListViewer(GuiScreen parent) {
        super(new JECContainer() {
            @Override
            protected void addSlots() {
                addSlotGroup(8, 8, 18, 20, 6, 4);
            }

            @Override
            public EnumSlotType getSlotType(int index) {
                return EnumSlotType.DISABLED;
            }
        }, parent, 6, 7);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiListViewer.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected List<Pair<String, Integer>> getSuitableRecipeIndex(List<Pair<String, Integer>> recipeList) {
        return recipeList;
    }
}
