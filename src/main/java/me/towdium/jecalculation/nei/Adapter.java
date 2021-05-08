package me.towdium.jecalculation.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.common.Loader;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.nei.adapter.AE2;
import me.towdium.jecalculation.nei.adapter.Forestry;
import me.towdium.jecalculation.nei.adapter.GregTech;
import me.towdium.jecalculation.nei.adapter.IAdapter;

import java.util.ArrayList;
import java.util.List;

public class Adapter {
    public static List<IAdapter> adapters = new ArrayList<>();

    public static void init() {
        if (Loader.isModLoaded("Forestry")) {
            JustEnoughCalculation.logger.info("Forestry installed");
            adapters.add(new Forestry());
        }
        if (Loader.isModLoaded("appliedenergistics2")) {
            JustEnoughCalculation.logger.info("ae2 installed");
            adapters.add(new AE2());
        }
        if (Loader.isModLoaded("gregtech")) {
            JustEnoughCalculation.logger.info("gregtech installed");
            adapters.add(new GregTech());
        }
    }

    public static void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        // raw inputs
        recipe.getIngredientStacks(index)
              .stream()
              .map((positionedStack) -> (Object[]) positionedStack.items)
              .forEach(inputs::add);

        // raw outputs
        PositionedStack resultStack = recipe.getResultStack(index);
        if (resultStack != null)
            outputs.add(resultStack.items);

        for (IAdapter adapter : adapters) {
            adapter.handleRecipe(recipe, index, inputs, outputs);
        }
    }
}
