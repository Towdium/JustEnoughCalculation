package me.towdium.jecalculation.nei.adapter;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gregtech.nei.GT_NEI_AssLineHandler;
import gregtech.nei.GT_NEI_DefaultHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class GregTech implements IAdapter {
    @Override
    public Set<String> getAllOverlayIdentifier() {
        return GT_Recipe.GT_Recipe_Map.sMappings.stream()
                                                .map(gt_recipe_map -> gt_recipe_map.mNEIName)
                                                .collect(Collectors.toSet());
    }

    @Override
    public void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        if (recipe instanceof GT_NEI_DefaultHandler || recipe instanceof GT_NEI_AssLineHandler) {
            handleDefault(recipe, index, inputs, outputs);
        }
    }

    protected void handleDefault(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        for (int i = 0; i < inputs.size(); i++) {
            Object[] objects = Arrays.stream(inputs.get(i)).map(o -> this.convertFluid((ItemStack) o)).toArray();
            inputs.set(i, objects);
        }
        List<PositionedStack> otherStacks = recipe.getOtherStacks(index);
        outputs.addAll(otherStacks.stream()
                                  .map(positionedStack -> positionedStack.items)
                                  .map(itemStacks -> Arrays.stream(itemStacks).map(this::convertFluid).toArray())
                                  .collect(Collectors.toList()));
    }

    protected Object convertFluid(ItemStack itemStack) {
        FluidStack fluidStack = GT_Utility.getFluidFromDisplayStack(itemStack);
        return fluidStack == null ? itemStack : fluidStack;
    }

    /**
     * get from `gregtech.nei.GT_NEI_AssLineHandler$CachedDefaultRecipe::Constructor`
     *
     * @param recipe  GT_Recipe
     * @param inputs  input list
     * @param outputs output list
     */
    @Deprecated
    private void getFromRecipe(GT_Recipe recipe, List<Object[]> inputs, List<Object[]> outputs) {
        for (int i = 0; i < recipe.mInputs.length; i++) {
            Object obj = recipe instanceof GT_Recipe.GT_Recipe_WithAlt ?
                         ((GT_Recipe.GT_Recipe_WithAlt) recipe).getAltRepresentativeInput(i) :
                         recipe.getRepresentativeInput(i);
            if (obj != null) {
                inputs.add(NEIServerUtils.extractRecipeItems(obj));
            }
        }
        if (recipe.mSpecialItems != null) {
            inputs.add(NEIServerUtils.extractRecipeItems(recipe.mSpecialItems));
        }

        if (recipe.getOutput(0) != null) {
            outputs.add(NEIServerUtils.extractRecipeItems(recipe.getOutput(0)));
        }

        for (FluidStack mFluidInput : recipe.mFluidInputs) {
            if (mFluidInput != null && mFluidInput.getFluid() != null) {
                inputs.add(new Object[]{mFluidInput});
            }
        }

        for (FluidStack mFluidOutput : recipe.mFluidOutputs) {
            if (mFluidOutput != null && mFluidOutput.getFluid() != null) {
                outputs.add(new Object[]{mFluidOutput});
            }
        }
    }
}
