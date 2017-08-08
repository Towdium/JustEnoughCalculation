package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.core.Recipe;
import me.towdium.jecalculation.gui.JECContainer;
import me.towdium.jecalculation.gui.JECGuiContainer;
import me.towdium.jecalculation.item.ItemLabel;
import me.towdium.jecalculation.util.Utilities;
import me.towdium.jecalculation.util.exception.IllegalPositionException;
import me.towdium.jecalculation.util.helpers.PlayerRecordHelper;
import me.towdium.jecalculation.util.wrappers.Pair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Author:  Towdium
 * Created: 2016/6/15.
 */
public class GuiListSearch extends GuiList {
    JECGuiContainer.JECGuiButton buttonMode;
    EnumMode mode = EnumMode.OUTPUT;

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
        buttonMode = new JECGuiContainer.JECGuiButton(14, 117 + guiLeft, 7 + guiTop, 52, 20, "", false).setLsnLeft(() -> {
            mode = EnumMode.values()[Utilities.circulate(mode.ordinal(), 4, true)];
            updateLayout();
        }).setLsnRight(() -> {
            mode = EnumMode.values()[Utilities.circulate(mode.ordinal(), 4, false)];
            updateLayout();
        });
        buttonList.add(buttonMode);
    }

    @Override
    protected List<Pair<String, Integer>> getSuitableRecipeIndex(List<Pair<String, Integer>> recipeList) {
        List<Pair<String, Integer>> buffer = new ArrayList<>();
        Function<Recipe, Boolean> func;
        ItemStack itemStack = inventorySlots.getSlot(20).getStack();
        switch (mode) {
            case INPUT:
                func = recipe -> recipe.getIndexInput(itemStack) != -1;
                break;
            case OUTPUT:
                func = recipe -> recipe.getIndexOutput(itemStack) != -1;
                break;
            case CATALYST:
                func = recipe -> recipe.getIndexCatalyst(itemStack) != -1;
                break;
            case ALL:
                func = recipe -> recipe.getIndexCatalyst(itemStack) != -1 || recipe.getIndexOutput(itemStack) != -1
                        || recipe.getIndexInput(itemStack) != -1;
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

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(
                new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/gui_list_search.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        ItemStack itemStack = inventorySlots.getSlot(20).getStack();
        if (!itemStack.isEmpty()) {
            drawString(fontRenderer,
                    Utilities.cutString(itemStack.getDisplayName(), 72, fontRenderer), 35, 13, 0xFFFFFF);
        }
    }

    @Override
    protected int getSizeSlot(int index) {
        return index == 20 ? 20 : 18;
    }

    @Override
    public void onItemStackSet(int index, ItemStack s) {
        super.onItemStackSet(index, s);
        ItemStack stack = inventorySlots.getSlot(index).getStack();
        if (!stack.isEmpty() && stack.getItem() instanceof ItemLabel && ItemLabel.getName(stack) == null) {
            inventorySlots.getSlot(index).putStack(s);
            Utilities.openGui(new GuiPickerLabelExisting(this, (itemStack) -> {
                inventorySlots.getSlot(index).putStack(itemStack);
                Utilities.openGui(this);
                updateLayout();
            }));
        }
        updateLayout();
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        buttonMode.displayString = localization(mode.toString().toLowerCase());
    }

    @Override
    protected int getDestSlot(int button) {
        return 20;
    }

    enum EnumMode {
        INPUT, OUTPUT, CATALYST, ALL
    }
}
