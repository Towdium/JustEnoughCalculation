package me.towdium.jecalculation.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.common.Loader;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.nei.adapter.*;

import java.util.ArrayList;
import java.util.List;

public class Adapter {
    public static List<IAdapter> adapters = new ArrayList<>();

    public static void init() {
        JustEnoughCalculation.logger.info("=====Just Enough Calculation Init Start=====");
        if (Loader.isModLoaded("Forestry")) {
            JustEnoughCalculation.logger.info("Forestry installed");
            adapters.add(new Forestry());
        }
        if (Loader.isModLoaded("appliedenergistics2")) {
            JustEnoughCalculation.logger.info("ae2 not supported");
            adapters.add(new AE2());
        }
        if (Loader.isModLoaded("gregtech")) {
            JustEnoughCalculation.logger.info("gregtech installed");
            adapters.add(new GregTech());
        }
        try {
            if (Loader.isModLoaded("miscutils")) {
                adapters.add(new GTPP());
                JustEnoughCalculation.logger.info("gt++ installed");
            }
        } catch (Exception e) {
            JustEnoughCalculation.logger.info("exception when installing gt++");
            e.printStackTrace();
        }
        if (Loader.isModLoaded("Avaritia")) {
            JustEnoughCalculation.logger.info("Avaritia installed");
            adapters.add(new Avaritia());
        }
        if (Loader.isModLoaded("EnderIO")) {
            JustEnoughCalculation.logger.info("EnderIO installed");
            adapters.add(new EnderIO());
        }
        if (Loader.isModLoaded("thaumcraftneiplugin")) {
            adapters.add(new Thaum());
            JustEnoughCalculation.logger.info("Thaumcraft installed");
        }
        JustEnoughCalculation.logger.info("=====Just Enough Calculation Init Finish=====");
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
