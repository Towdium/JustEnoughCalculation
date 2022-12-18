package me.towdium.jecalculation.nei;

import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.GuiRecipeTab;
import codechicken.nei.recipe.HandlerInfo;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiRecipe;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class JecaOverlayHandler implements IOverlayHandler {
    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        if (firstGui instanceof JecaGui) {
            Class<?> context = recipe.getClass();
            JecaGui gui = (JecaGui) firstGui;
            if (gui.root instanceof GuiRecipe) {
                ((GuiRecipe) gui.root).transfer(convertRecipe(recipe, recipeIndex, context), context);
            } else {
                GuiRecipe guiRecipe = new GuiRecipe();
                JecaGui.displayGui(true, true, guiRecipe);
                guiRecipe.transfer(convertRecipe(recipe, recipeIndex, context), context);
            }
        }
    }

    private static EnumMap<Recipe.IO, List<Trio<ILabel, CostList, CostList>>> convertRecipe(
            IRecipeHandler recipe, int recipeIndex, Class<?> context) {
        // item disamb raw
        EnumMap<Recipe.IO, List<Trio<ILabel, CostList, CostList>>> merged = new EnumMap<>(Recipe.IO.class);

        // merge recipe input, output and catalysts
        List<Object[]> recipeInputs = new ArrayList<>();
        List<Object[]> recipeOutputs = new ArrayList<>();
        Adapter.handleRecipe(recipe, recipeIndex, recipeInputs, recipeOutputs);

        // input
        recipeInputs.forEach(i -> merge(merged, Arrays.asList(i), context, Recipe.IO.INPUT));
        // output
        recipeOutputs.forEach(o -> merge(merged, Arrays.asList(o), context, Recipe.IO.OUTPUT));

        JecaOverlayHandler.getCatalyst(recipe).ifPresent(catalyst -> {
            List<ItemStack> catalysts = Collections.singletonList(catalyst);
            merge(merged, catalysts, context, Recipe.IO.CATALYST);
        });

        return merged;
    }

    private static void merge(
            EnumMap<Recipe.IO, List<Trio<ILabel, CostList, CostList>>> dst,
            List<?> objs,
            Class<?> context,
            Recipe.IO type) {
        List<ILabel> list = objs.stream().map(ILabel.Converter::from).collect(Collectors.toList());
        if (list.isEmpty()) return;
        ILabel rep = list.get(0).copy();
        if (type == Recipe.IO.INPUT && list.size() != 1) rep = ILabel.CONVERTER.first(list, context);
        ILabel fin = rep;

        dst.computeIfAbsent(type, i -> new ArrayList<>()).stream()
                .filter(p -> {
                    CostList cl = new CostList(list);
                    if (p.three.equals(cl)) {
                        ILabel.MERGER.merge(p.one, fin).ifPresent(i -> p.one = i);
                        p.two = CostList.merge(p.two, cl, true);
                        return true;
                    } else return false;
                })
                .findAny()
                .orElseGet(() -> {
                    Trio<ILabel, CostList, CostList> ret = new Trio<>(fin, new CostList(list), new CostList(list));
                    dst.get(type).add(ret);
                    return ret;
                });
    }

    private static Optional<ItemStack> getCatalyst(@Nonnull IRecipeHandler handler) {
        if (!NEIPlugin.isCatalystEnabled()) {
            return Optional.empty();
        }
        final String handlerName = handler.toString().split("@")[0];
        final String handlerID;
        if (handler instanceof TemplateRecipeHandler) {
            handlerID = (((TemplateRecipeHandler) handler).getOverlayIdentifier());
        } else {
            handlerID = null;
        }
        HandlerInfo info = GuiRecipeTab.getHandlerInfo(handlerName, handlerID);

        if (info == null) {
            return Optional.empty();
        }
        ItemStack itemStack = info.getItemStack();
        return Optional.ofNullable(itemStack);
    }
}
