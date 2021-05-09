package me.towdium.jecalculation.nei.adapter;

import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import gregtech.api.util.GTPP_Recipe;
import gtPlusPlus.nei.*;
import me.towdium.jecalculation.JustEnoughCalculation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * don't work, why?
 */
@ParametersAreNonnullByDefault
public class GTPP extends GregTech {

    private final static Set<Class<? extends TemplateRecipeHandler>> defaultHandlers = Stream.of(
            GTPP_NEI_DefaultHandler.class, GT_NEI_VacFurnace.class, GT_NEI_RFPP.class,
            GT_NEI_multiCentriElectroFreezer.class, GT_NEI_MillingMachine.class, GT_NEI_FluidReactor.class,
            GT_NEI_FlotationCell.class, DecayableRecipeHandler.class).collect(Collectors.toSet());

    @Override
    public Set<String> getAllOverlayIdentifier() {
        Set<String> recipeNames = GTPP_Recipe.GT_Recipe_Map.sMappings.stream()
                                                                     .map(gt_recipe_map -> gt_recipe_map.mNEIName)
                                                                     .collect(Collectors.toSet());
        recipeNames.add("GTPP_Decayables");
        JustEnoughCalculation.logger.info("GTPP recipe handlers cecipe name");
        for (String recipeName : recipeNames) {
            JustEnoughCalculation.logger.info("  " + recipeName);
        }
        return recipeNames;
    }

    @Override
    public void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        if (defaultHandlers.stream().anyMatch(aClass -> aClass.isInstance(recipe))) {
            handleDefault(recipe, index, inputs, outputs);
        }
    }
}
