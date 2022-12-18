package me.towdium.jecalculation.nei.adapter;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import forestry.core.recipes.nei.PositionedFluidTank;
import forestry.core.recipes.nei.RecipeHandlerBase;
import forestry.factory.recipes.nei.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

@ParametersAreNonnullByDefault
public class Forestry implements IAdapter {
    @Override
    public void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        if (recipe instanceof RecipeHandlerBase) {
            RecipeHandlerBase base = (RecipeHandlerBase) recipe;
            bottler(base, index, inputs);
            carpenter(base, index, inputs);
            centrifuge(base, index, outputs);
            fabricator(base, index, inputs);
            fermenter(base, index, inputs, outputs);
            moistener(base, index, inputs, outputs);
            squeezer(base, index, inputs, outputs);
            still(base, index, inputs, outputs);
        }
    }

    private void bottler(RecipeHandlerBase recipe, int recipeIndex, List<Object[]> inputs) {
        if (recipe instanceof NEIHandlerBottler) {
            List<List<FluidStack>> fluids = getFluids(recipe, recipeIndex);
            if (fluids.size() >= 1) inputs.add(fluids.get(0).toArray());
        }
    }

    private void carpenter(RecipeHandlerBase recipe, int recipeIndex, List<Object[]> inputs) {
        if (recipe instanceof NEIHandlerCarpenter) {
            List<List<FluidStack>> fluids = getFluids(recipe, recipeIndex);
            if (fluids.size() >= 1) inputs.add(fluids.get(0).toArray());
        }
    }

    private void centrifuge(RecipeHandlerBase recipe, int recipeIndex, List<Object[]> outputs) {
        if (recipe instanceof NEIHandlerCentrifuge) {
            List<PositionedStack> otherStacks = recipe.getOtherStacks(recipeIndex);
            otherStacks.stream().map(positionedStack -> positionedStack.items).forEach(outputs::add);
        }
    }

    private void fabricator(RecipeHandlerBase recipe, int recipeIndex, List<Object[]> inputs) {
        if (recipe instanceof NEIHandlerFabricator) {
            List<List<FluidStack>> fluids = getFluids(recipe, recipeIndex);
            if (fluids.size() >= 1) inputs.add(fluids.get(0).toArray());
        }
    }

    private void fermenter(RecipeHandlerBase recipe, int recipeIndex, List<Object[]> inputs, List<Object[]> outputs) {
        if (recipe instanceof NEIHandlerFermenter) {
            List<PositionedFluidTank> tanks = getTanks(recipe, recipeIndex);
            tanks.forEach(tank -> {
                Object[] fluids =
                        Arrays.stream(tank.tanks).map(FluidTank::getFluid).toArray();
                if (tank.position.x == 30) {
                    // source
                    inputs.add(fluids);
                } else {
                    // outputs
                    outputs.add(fluids);
                }
            });
        }
    }

    private void moistener(RecipeHandlerBase recipe, int recipeIndex, List<Object[]> inputs, List<Object[]> outputs) {
        if (recipe instanceof NEIHandlerMoistener) {
            List<List<FluidStack>> fluids = getFluids(recipe, recipeIndex);
            if (fluids.size() >= 1) inputs.add(fluids.get(0).toArray());
            List<PositionedStack> otherStacks = recipe.getOtherStacks(recipeIndex);
            otherStacks.forEach(positionedStack -> {
                ItemStack[] items = positionedStack.items;
                if (positionedStack.relx == 34) {
                    // item
                    inputs.add(items);
                } else {
                    // product
                    outputs.add(items);
                }
            });
        }
    }

    private void squeezer(RecipeHandlerBase recipe, int recipeIndex, List<Object[]> inputs, List<Object[]> outputs) {
        if (recipe instanceof NEIHandlerSqueezer) {
            List<List<FluidStack>> fluids = getFluids(recipe, recipeIndex);
            if (fluids.size() >= 1) outputs.add(fluids.get(0).toArray());
        }
    }

    private void still(RecipeHandlerBase recipe, int recipeIndex, List<Object[]> inputs, List<Object[]> outputs) {
        if (recipe instanceof NEIHandlerStill) {
            getTanks(recipe, recipeIndex).forEach(tank -> {
                Object[] fluids =
                        Arrays.stream(tank.tanks).map(FluidTank::getFluid).toArray();
                if (tank.position.x == 30) {
                    // input
                    inputs.add(fluids);
                } else {
                    // output
                    outputs.add(fluids);
                }
            });
        }
    }

    /**
     * It won't check the type
     *
     * @param recipe      recipe
     * @param recipeIndex index
     * @return fluid tanks
     */
    private List<List<FluidStack>> getFluids(RecipeHandlerBase recipe, int recipeIndex) {
        return ((RecipeHandlerBase.CachedBaseRecipe) recipe.arecipes.get(recipeIndex))
                .getFluidTanks().stream()
                        .map(positionedFluidTank -> Arrays.stream(positionedFluidTank.tanks)
                                .map(FluidTank::getFluid)
                                .collect(Collectors.toList()))
                        .collect(Collectors.toList());
    }

    private List<PositionedFluidTank> getTanks(RecipeHandlerBase recipe, int recipeIndex) {
        return ((RecipeHandlerBase.CachedBaseRecipe) recipe.arecipes.get(recipeIndex)).getFluidTanks();
    }
}
