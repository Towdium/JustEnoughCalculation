package pers.towdium.just_enough_calculation.plugin;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import pers.towdium.just_enough_calculation.gui.guis.GuiEditor;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.function.TriConsumer;
import pers.towdium.just_enough_calculation.util.wrappers.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Towdium
 */
public class JECRecipeTransferHandler implements IRecipeTransferHandler {
    String recipeUID;
    Class<? extends Container> container;

    public JECRecipeTransferHandler(String recipeUID, Class<? extends Container> container) {
        this.recipeUID = recipeUID;
        this.container = container;
    }

    @Override
    public Class<? extends Container> getContainerClass() {
        return container;
    }

    @Override
    public String getRecipeCategoryUid() {
        return recipeUID;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(@Nonnull Container container, @Nonnull IRecipeLayout iRecipeLayout, @Nonnull EntityPlayer entityPlayer, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            BiConsumer<List<ItemStack>, ItemStack> merger = (itemStacks, stack) -> {
                Singleton<Boolean> flag = new Singleton<>(false);
                itemStacks.forEach(itemStack ->
                        flag.value = flag.value || ItemStackHelper.mergeStack(itemStack, stack, true, false) != null
                );
                if (!flag.value)
                    itemStacks.add(stack);
            };
            List<ItemStack> outputStacks = new ArrayList<>();
            List<ItemStack> inputStacks = new ArrayList<>();

            iRecipeLayout.getItemStacks().getGuiIngredients().values().forEach(ingredient -> {
                if (ingredient.getAllIngredients().size() != 0)
                    merger.accept(ingredient.isInput() ? inputStacks :
                            outputStacks, ItemStackHelper.toItemStackJEC(ingredient.getAllIngredients().get(0).copy()));
            });
            iRecipeLayout.getFluidStacks().getGuiIngredients().values().forEach(ingredient -> {
                if (ingredient.getAllIngredients().size() != 0)
                    merger.accept(ingredient.isInput() ? inputStacks :
                            outputStacks, ItemStackHelper.toItemStackJEC(ingredient.getAllIngredients().get(0).copy()));
            });

            Iterator<ItemStack> iterator = JEIPlugin.recipeRegistry.getCraftingItems(
                    JEIPlugin.recipeRegistry.getRecipeCategories(Collections.singletonList(recipeUID)).get(0)
            ).iterator();

            Minecraft mc = Minecraft.getMinecraft();
            RecipesGui gui = (RecipesGui) mc.currentScreen;
            if (gui == null) {
                return null;
            }
            GuiScreen parent = gui.getParentScreen();
            GuiEditor editor = parent instanceof GuiEditor ? (GuiEditor) parent : new GuiEditor(parent, null);

            TriConsumer<Integer, Integer, List<ItemStack>> arranger = (start, end, stacks) -> {
                for (int i = start; i <= end; i++) {
                    editor.inventorySlots.getSlot(i).putStack(stacks.size() > i - start ? stacks.get(i - start) : null);
                }
            };

            mc.displayGuiScreen(editor);
            arranger.accept(0, 3, outputStacks);
            arranger.accept(8, 19, inputStacks);
            arranger.accept(4, 7, iterator.hasNext() ?
                    Collections.singletonList(ItemStackHelper.toItemStackJEC(iterator.next())) : new ArrayList<>());
        }
        return null;
    }
}
