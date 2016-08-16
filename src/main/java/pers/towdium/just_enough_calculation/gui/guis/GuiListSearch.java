package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.util.PlayerRecordHelper;
import pers.towdium.just_enough_calculation.util.Utilities;
import pers.towdium.just_enough_calculation.util.exception.IllegalPositionException;
import pers.towdium.just_enough_calculation.util.wrappers.Pair;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Author:  Towdium
 * Created: 2016/6/15.
 */
public class GuiListSearch extends GuiList {
    GuiButton buttonMode;
    EnumMode mode = EnumMode.OUT;

    public GuiListSearch(GuiScreen parent, ItemStack itemStack) {
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
        inventorySlots.getSlot(20).putStack(itemStack);
    }

    @Override
    public void init() {
        super.init();
        buttonMode = new GuiButton(14, 117 + guiLeft, 7 + guiTop, 52, 20, "");
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
            case CAT:
                func = recipe -> recipe.getIndexCatalyst(itemStack) != -1;
                break;
            case ALL:
                func = recipe -> recipe.getIndexCatalyst(itemStack) != -1 || recipe.getIndexOutput(itemStack) != -1 || recipe.getIndexInput(itemStack) != -1;
                break;
            default:
                throw new IllegalPositionException();
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
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiListSearch.png"));
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
    public void onItemStackSet(int index) {
        super.onItemStackSet(index);
        updateLayout();
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        buttonMode.displayString = mode.getDisplayString();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 14) {
            mode = EnumMode.values()[Utilities.circulate(mode.ordinal(), 4, true)];
            updateLayout();
        } else
            super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (buttonMode.isMouseOver() && mouseButton == 1) {
            mode = EnumMode.values()[Utilities.circulate(mode.ordinal(), 4, false)];
            updateLayout();
            mc.thePlayer.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2f, 1f);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    enum EnumMode {
        IN, OUT, CAT, ALL;

        public String getDisplayString() {
            switch (this) {
                case IN:
                    return "Input";
                case OUT:
                    return "Output";
                case CAT:
                    return "Catalyst";
                case ALL:
                    return "All";
                default:
                    throw new IllegalPositionException();
            }
        }
    }
}
