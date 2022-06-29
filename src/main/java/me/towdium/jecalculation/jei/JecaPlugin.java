package me.towdium.jecalculation.jei;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.Recipe.IO;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiRecipe;
import me.towdium.jecalculation.utils.wrappers.Trio;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-8-23.
 */
@JeiPlugin
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JecaPlugin implements IModPlugin {
    public static IJeiRuntime runtime;

    public static ILabel getLabelUnderMouse() {
        var ref = new Object(){
          Object o = null;
        };
        runtime.getIngredientListOverlay().getIngredientUnderMouse().ifPresent(ingredient -> ref.o = ingredient.getIngredient());
        runtime.getBookmarkOverlay().getIngredientUnderMouse().ifPresent(ingredient -> ref.o = ingredient.getIngredient());
        return ILabel.Converter.from(ref.o);
    }

    public static boolean isFocused() {
        return runtime.getIngredientListOverlay().hasKeyboardFocus();
    }

    public static void showRecipe(ILabel l) {
        Object rep = l.getRepresentation();
        if (rep != null) {
            runtime.getRecipesGui().show(runtime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.OUTPUT, runtime.getIngredientManager().getIngredientType(rep), rep));
        }
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(JustEnoughCalculation.MODID, "general");
    }


    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addUniversalRecipeTransferHandler(new TransferHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    public static class TransferHandler implements IRecipeTransferHandler<JecaGui.ContainerTransfer, Recipe> {
        @Override
        public Class<JecaGui.ContainerTransfer> getContainerClass() {
            return JecaGui.ContainerTransfer.class;
        }

        @Override
        public Class<Recipe> getRecipeClass() {
            return Recipe.class;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(JecaGui.ContainerTransfer container, Recipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
            if (doTransfer) {
                Class<?> context = runtime.getRecipeManager().createRecipeCategoryLookup().get().filter(category -> category.getRecipeType().getRecipeClass() == recipe.getClass()).findFirst().getClass();
                JecaGui gui = container.getGui();
                if (gui.root instanceof GuiRecipe) {
                    ((GuiRecipe) gui.root).transfer(convertRecipe(recipeSlots, context), context);
                } else {
                    GuiRecipe guiRecipe = new GuiRecipe();
                    JecaGui.displayGui(guiRecipe, JecaGui.getLast());
                    guiRecipe.transfer(convertRecipe(recipeSlots, context), context);
                }
            }
            return null;
        }


        private static EnumMap<IO, List<Trio<ILabel, CostList, CostList>>> convertRecipe(
                IRecipeSlotsView recipe, Class<?> context) {
            EnumMap<IO, List<Trio<ILabel, CostList, CostList>>> merged = new EnumMap<>(IO.class);  // item disamb raw
            merge(merged, recipe.getSlotViews(RecipeIngredientRole.INPUT).stream().flatMap(IRecipeSlotView::getAllIngredients).map(ITypedIngredient::getIngredient).toList(), context, IO.INPUT);
            merge(merged, recipe.getSlotViews(RecipeIngredientRole.OUTPUT).stream().flatMap(IRecipeSlotView::getAllIngredients).map(ITypedIngredient::getIngredient).toList(), context, IO.OUTPUT);
            merge(merged, recipe.getSlotViews(RecipeIngredientRole.CATALYST).stream().flatMap(IRecipeSlotView::getAllIngredients).map(ITypedIngredient::getIngredient).toList(), context, IO.CATALYST);
            return merged;
        }

        private static void merge(EnumMap<IO, List<Trio<ILabel, CostList, CostList>>> dst, List<?> objs, Class<?> context, IO type) {
            List<ILabel> list = objs.stream().map(ILabel.Converter::from).collect(Collectors.toList());
            if (list.isEmpty()) return;
            ILabel rep = list.get(0).copy();
            if (type == IO.INPUT && list.size() != 1) rep = ILabel.CONVERTER.first(list, context);
            ILabel fin = rep;

            dst.computeIfAbsent(type, i -> new ArrayList<>()).stream().filter(p -> {
                CostList cl = new CostList(list);
                if (p.three.equals(cl)) {
                    ILabel.MERGER.merge(p.one, fin).ifPresent(i -> p.one = i);
                    p.two = p.two.merge(cl, true, false);
                    return true;
                } else return false;
            }).findAny().orElseGet(() -> {
                Trio<ILabel, CostList, CostList> ret = new Trio<>(fin, new CostList(list), new CostList(list));
                dst.get(type).add(ret);
                return ret;
            });
        }
    }
}
