package me.towdium.jecalculation.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.common.Loader;
import java.util.ArrayList;
import java.util.List;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.nei.adapter.*;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.item.ItemStack;

public class Adapter {
    public static List<IAdapter> adapters = new ArrayList<>();

    public static Object convertFluid(ItemStack itemStack) {
        String name = Utilities.getName(itemStack);
        String modId = name.substring(0, name.indexOf(":"));
        String itemId = name.substring(name.indexOf(":") + 1);
        if (Loader.isModLoaded("gregtech")) {
            if (GregTech6.isGT6()) {
                return GregTech6.convertFluid(itemStack);
            } else {
                return GregTech.convertFluid(itemStack);
            }
        }
        return itemStack;
    }

    public static void init() {
        JustEnoughCalculation.logger.info("=====Just Enough Calculation Init Start=====");
        if (Loader.isModLoaded("Forestry")) {
            JustEnoughCalculation.logger.info("Forestry detected");
            adapters.add(new Forestry());
        }
        if (Loader.isModLoaded("appliedenergistics2")) {
            JustEnoughCalculation.logger.info("ae2 detected");
            adapters.add(new AE2());
        }
        if (Loader.isModLoaded("gregtech")) {
            if (GregTech6.isGT6()) {
                JustEnoughCalculation.logger.info("gregtech6 detected");
                adapters.add(new GregTech6());
            } else {
                JustEnoughCalculation.logger.info("gregtech5 detected");
                adapters.add(new GregTech());
            }
        }
        try {
            if (Loader.isModLoaded("miscutils")) {
                adapters.add(new GTPP());
                JustEnoughCalculation.logger.info("gt++ detected");
            }
        } catch (Exception e) {
            JustEnoughCalculation.logger.error("Init error with gt++");
            e.printStackTrace();
        }
        if (Loader.isModLoaded("Avaritia")) {
            JustEnoughCalculation.logger.info("Avaritia detected");
            adapters.add(new Avaritia());
        }
        if (Loader.isModLoaded("EnderIO")) {
            JustEnoughCalculation.logger.info("EnderIO detected");
            adapters.add(new EnderIO());
        }
        if (Loader.isModLoaded("thaumcraftneiplugin")) {
            adapters.add(new Thaum());
            JustEnoughCalculation.logger.info("Thaumcraft detected");
        }
        JustEnoughCalculation.logger.info("=====Just Enough Calculation Init Finish=====");
    }

    public static void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        // raw inputs
        recipe.getIngredientStacks(index).stream()
                .map((positionedStack) -> (Object[]) positionedStack.items)
                .forEach(inputs::add);

        // raw outputs
        PositionedStack resultStack = recipe.getResultStack(index);
        if (resultStack != null) outputs.add(resultStack.items);

        try {
            for (IAdapter adapter : adapters) {
                adapter.handleRecipe(recipe, index, inputs, outputs);
            }
        } catch (Exception e) {
            Utilities.addChatMessage(Utilities.ChatMessage.RECIPE_TRANSFER_ERROR);
            JustEnoughCalculation.logger.error(
                    "Exception when handling recipe: " + recipe.getClass().getName());
            e.printStackTrace();
        }
    }
}
