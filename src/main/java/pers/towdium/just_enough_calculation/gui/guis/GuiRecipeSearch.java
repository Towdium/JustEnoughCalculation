package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.util.PlayerRecordHelper;
import pers.towdium.just_enough_calculation.util.Utilities;
import pers.towdium.just_enough_calculation.util.wrappers.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Author:  Towdium
 * Created: 2016/6/15.
 */
public class GuiRecipeSearch extends GuiRecipeList {
    GuiButton buttonMode;
    EnumMode mode = EnumMode.OUT;

    public GuiRecipeSearch(GuiScreen parent, ItemStack itemStack) {
        super(new JECContainer() {
            @Override
            protected void addSlots() {
                addSlotGroup(8, 32, 18, 20, 5, 4);
                addSlotSingle(9, 9);
            }

            @Override
            public EnumSlotType getSlotType(int index) {
                return index == 20 ? EnumSlotType.SELECT : EnumSlotType.DISABLED;
            }
        }, parent, 5, 31);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonMode = new GuiButton(12, 117 + guiLeft, 7 + guiTop, 52, 20, "mode...");
        buttonList.add(buttonMode);
    }

    @Override
    protected List<Pair<String, Integer>> getSuitableRecipeIndex(List<Pair<String, Integer>> recipeList) {
        List<Pair<String, Integer>> buffer = new ArrayList<>();
        Function<Recipe, Boolean> func;
        ItemStack itemStack = inventorySlots.getSlot(20).getStack();
        switch (mode) {
            case IN:
                func = recipe -> recipe.getIndexInput(itemStack) != -1;
                break;
            case OUT:
                func = recipe -> recipe.getIndexOutput(itemStack) != -1;
                break;
            default:
                func = recipe -> recipe.getIndexInput(itemStack) != -1 || recipe.getIndexOutput(itemStack) != -1;
                break;
        }
        Consumer<Pair<String, Integer>> operator = (pair) -> {
            Recipe recipe = PlayerRecordHelper.getRecipe(pair.one, pair.two);
            if (func.apply(recipe))
                buffer.add(pair);
        };
        recipeList.forEach(operator);
        return buffer;
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
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        ItemStack itemStack = inventorySlots.getSlot(20).getStack();
        if (itemStack != null) {
            drawString(fontRendererObj, Utilities.cutString(itemStack.getDisplayName(), 72, fontRendererObj), 35, 13, 0xFFFFFF);
        }
    }

    @Override
    protected int getSizeSlot(int index) {
        return index == 20 ? 20 : 18;
    }

    @Override
    protected void onItemStackSet(int index) {
        super.onItemStackSet(index);
        updateLayout();
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
