package me.towdium.jecalculation.nei.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;

@ParametersAreNonnullByDefault
public class GregTech6 implements IAdapter {

    public static boolean isGT6() {
        try {
            Class<?> buildInfoclz = Class.forName("gregtech.BuildInfo");
            Field versionField = buildInfoclz.getField("version");
            String version = (String) versionField.get(null);
            return version.startsWith("6.");
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Set<String> getAllOverlayIdentifier() {
        Set<String> recipeNames = new HashSet<>();
        try {
            Class<?> recipeMapClz = Class.forName("gregapi.recipes.Recipe$RecipeMap");
            Field recipeMapField = recipeMapClz.getDeclaredField("RECIPE_MAPS");
            Field mNameNEIField = recipeMapClz.getDeclaredField("mNameNEI");
            Map maps = (Map) recipeMapField.get(null);
            for (Object value : maps.values()) {
                try {
                    recipeNames.add((String) mNameNEIField.get(value));
                } catch (IllegalAccessException ignored) {}
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return recipeNames;
    }

    public static final Class<?> gtRecipeMap;
    public static final Class<?> gtRecipeMapCachedRecipe;
    public static final Class<?> gtRecipe;

    static {
        Class<?> gtRM = null;
        Class<?> gtRMCR = null;
        Class<?> gtR = null;
        try {
            gtRM = Class.forName("gregapi.NEI_RecipeMap");
            gtRMCR = Class.forName("gregapi.NEI_RecipeMap$CachedDefaultRecipe");
            gtR = Class.forName("gregapi.recipes.Recipe");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        gtRecipeMap = gtRM;
        gtRecipeMapCachedRecipe = gtRMCR;
        gtRecipe = gtR;
    }

    @Override
    public void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        if (gtRecipeMap.isInstance(recipe)) {
            TemplateRecipeHandler a = (TemplateRecipeHandler) recipe;
            TemplateRecipeHandler.CachedRecipe cachedRecipe = a.arecipes.get(index);
            try {
                Field mRecipe = gtRecipeMapCachedRecipe.getDeclaredField("mRecipe");
                Object re = mRecipe.get(cachedRecipe);
                Field mInputs = gtRecipe.getDeclaredField("mInputs");
                Field mOutputs = gtRecipe.getDeclaredField("mOutputs");
                Field mFluidInputs = gtRecipe.getDeclaredField("mFluidInputs");
                Field mFluidOutputs = gtRecipe.getDeclaredField("mFluidOutputs");
                ItemStack[] input = (ItemStack[]) mInputs.get(re);
                ItemStack[] output = (ItemStack[]) mOutputs.get(re);
                FluidStack[] fluidInputs = (FluidStack[]) mFluidInputs.get(re);
                FluidStack[] fluidOutputs = (FluidStack[]) mFluidOutputs.get(re);
                inputs.clear();
                inputs.addAll(
                    Stream.concat(Arrays.stream(input), Arrays.stream(fluidInputs))
                        .map(i -> new Object[] { i })
                        .collect(Collectors.toList()));
                outputs.clear();
                outputs.addAll(
                    Stream.concat(Arrays.stream(output), Arrays.stream(fluidOutputs))
                        .map(o -> new Object[] { o })
                        .collect(Collectors.toList()));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            // for (int i = 0; i < inputs.size(); i++) {
            // Object[] objects = Arrays.stream(inputs.get(i)).map(o ->
            // GregTech6.convertFluid((ItemStack) o)).toArray();
            // inputs.set(i, objects);
            // }
            // List<PositionedStack> otherStacks = recipe.getOtherStacks(index);
            // outputs.addAll(otherStacks
            // .stream()
            // .map(stack -> stack.items)
            // .map(is -> Arrays.stream(is).map(GregTech6::convertFluid).toArray())
            // .collect(Collectors.toList()));
        }
    }

    public static Object convertFluid(ItemStack itemStack) {
        FluidStack fluidStack = getFluidFromDisplayStack(itemStack);
        return fluidStack == null ? itemStack : fluidStack;
    }

    private static FluidStack getFluidFromDisplayStack(ItemStack aDisplayStack) {
        try {
            Class<?> itemListClz = Class.forName("gregapi.data.FL");
            Method getFluidMethod = itemListClz.getDeclaredMethod("getFluid", ItemStack.class, boolean.class);
            return (FluidStack) getFluidMethod.invoke(null, aDisplayStack, true);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
            | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
