package me.towdium.jecalculation.nei.adapter;

import codechicken.nei.recipe.IRecipeHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * don't work, why?
 */
@ParametersAreNonnullByDefault
public class GTPP extends GregTech {

    private final static Set<Class<?>> defaultHandlers;

    static {
        List<String> handlers = Stream.of(
                // gtpp after 2020/5/30
                "GTPP_NEI_DefaultHandler", "GT_NEI_VacFurnace", "GT_NEI_RFPP", "GT_NEI_multiCentriElectroFreezer",
                "GT_NEI_MillingMachine", "GT_NEI_FluidReactor", "GT_NEI_FlotationCell", "DecayableRecipeHandler",
                // gtpp before 2020/5/30
                "GT_NEI_DefaultHandler", "GT_NEI_Dehydrator", "GT_NEI_MultiBlockHandler", "GTPP_NEI_CustomMapHandler",
                "GTPP_NEI_DefaultHandlerEx"

        ).map(name -> "gtPlusPlus.nei." + name).collect(Collectors.toList());

        defaultHandlers = new HashSet<>();
        for (String handler : handlers) {
            try {
                defaultHandlers.add(Class.forName(handler));
            } catch (ClassNotFoundException e) {
                //                e.printStackTrace();
            }
        }
    }

    @Override
    public Set<String> getAllOverlayIdentifier() {
        Set<String> recipeNames = new HashSet<>();
        try {
            // add as many recipe as possible

            recipeNames.addAll(
                    this.reflectGetRecipeMapNEIName("gregtech.api.util.GTPP_Recipe$GTPP_Recipe_Map", "sMappings"));

            recipeNames.addAll(this.reflectGetRecipeMapNEIName("gregtech.api.util.GTPP_Recipe$GTPP_Recipe_Map_Internal",
                                                               "sMappingsEx"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // which one?
        recipeNames.add("GTPP_Decayables");
        recipeNames.add("Decayables");
        return recipeNames;
    }

    @Override
    public void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        if (defaultHandlers.stream().anyMatch(aClass -> aClass.isInstance(recipe))) {
            handleDefault(recipe, index, inputs, outputs);
        }
    }
}
