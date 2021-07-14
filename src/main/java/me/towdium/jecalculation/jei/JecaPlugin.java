package me.towdium.jecalculation.jei;

import mcp.MethodsReturnNonnullByDefault;
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
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
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
        Wrapper<Object> o = new Wrapper<>(null);
        o.push(runtime.getIngredientListOverlay().getIngredientUnderMouse());
        o.push(runtime.getBookmarkOverlay().getIngredientUnderMouse());
        return ILabel.Converter.from(o.value);
    }

    public static boolean isFocused() {
        return runtime.getIngredientListOverlay().hasKeyboardFocus();
    }

    public static void showRecipe(ILabel l) {
        Object rep = l.getRepresentation();
        if (rep != null) {
            runtime.getRecipesGui().show(runtime.getRecipeManager().createFocus(IFocus.Mode.OUTPUT, rep));
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

    public static class TransferHandler implements IRecipeTransferHandler<JecaGui.ContainerTransfer> {
        @Override
        public Class<JecaGui.ContainerTransfer> getContainerClass() {
            return JecaGui.ContainerTransfer.class;
        }

        @Nullable
        @Override
        public IRecipeTransferError transferRecipe(
                JecaGui.ContainerTransfer container, IRecipeLayout layout,
                PlayerEntity player, boolean maxTransfer, boolean doTransfer) {

            if (doTransfer) {
                Class<?> context = layout.getRecipeCategory().getClass();
                JecaGui gui = container.getGui();
                if (gui.root instanceof GuiRecipe) {
                    ((GuiRecipe) gui.root).transfer(convertRecipe(layout, context), context);
                } else {
                    GuiRecipe guiRecipe = new GuiRecipe();
                    JecaGui.displayGui(guiRecipe, JecaGui.getLast());
                    guiRecipe.transfer(convertRecipe(layout, context), context);
                }
            }
            return null;
        }

        @SuppressWarnings("rawtypes")
        private static EnumMap<IO, List<Trio<ILabel, CostList, CostList>>> convertRecipe(
                IRecipeLayout recipe, Class<?> context) {
            EnumMap<IO, List<Trio<ILabel, CostList, CostList>>> merged = new EnumMap<>(IO.class);  // item disamb raw
            List<IIngredientType<?>> types = new ArrayList<>();
            for (IIngredientType i : runtime.getIngredientManager().getRegisteredIngredientTypes()) {
                types.add((IIngredientType<?>) i);
            }

            types.stream().map(recipe::getIngredientsGroup)
                    .flatMap(i -> i.getGuiIngredients().values().stream())
                    .forEach(i -> merge(merged, i.getAllIngredients(), context, IO.isInput(i.isInput())));
            List<Object> catalysts = JecaPlugin.runtime.getRecipeManager().getRecipeCatalysts(recipe.getRecipeCategory());
            merge(merged, catalysts, context, IO.CATALYST);
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
