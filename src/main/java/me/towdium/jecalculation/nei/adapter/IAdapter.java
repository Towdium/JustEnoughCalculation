package me.towdium.jecalculation.nei.adapter;

import codechicken.nei.recipe.IRecipeHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface IAdapter {
    void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs);
}
