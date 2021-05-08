package me.towdium.jecalculation.nei.adapter;

import codechicken.nei.recipe.IRecipeHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
public interface IAdapter {
    default Set<String> getAllOverlayIdentifier() {
        return Collections.emptySet();
    };

    void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs);
}
