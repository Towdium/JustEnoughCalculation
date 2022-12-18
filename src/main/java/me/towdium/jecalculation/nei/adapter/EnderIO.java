package me.towdium.jecalculation.nei.adapter;

import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@ParametersAreNonnullByDefault
public class EnderIO implements IAdapter {

    @Override
    public Set<String> getAllOverlayIdentifier() {
        return new HashSet<>(Arrays.asList(
                "EnderIOAlloySmelter",
                "EIOEnchanter",
                "EnderIOSagMill",
                "EnderIOSliceAndSplice",
                "EnderIOSoulBinder",
                "EnderIOVat"));
    }

    private static final Set<Class<?>> defaultHandlers;
    private static final Class<?> vat;

    static {
        List<String> handlers = Stream.of("SagMillRecipeHandler")
                .map(name -> "crazypants.enderio.nei." + name)
                .collect(Collectors.toList());
        defaultHandlers = new HashSet<>();
        for (String handler : handlers) {
            try {
                defaultHandlers.add(Class.forName(handler));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Class<?> innerVat = null;
        try {
            innerVat = Class.forName("crazypants.enderio.nei.VatRecipeHandler");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        vat = innerVat;
    }

    @Override
    public void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        if (defaultHandlers.stream().anyMatch(aClass -> aClass.isInstance(recipe))) {
            List<ItemStack[]> otherStacks = recipe.getOtherStacks(index).stream()
                    .map(positionedStack -> positionedStack.items)
                    .collect(Collectors.toList());
            outputs.addAll(otherStacks);
        } else {
            try {
                if (vat != null && vat.isInstance(recipe)) {
                    Class<?> vatInnerRecipe = Class.forName("crazypants.enderio.nei.VatRecipeHandler$InnerVatRecipe");

                    Field arecipes = vat.getField("arecipes");
                    @SuppressWarnings("unchecked")
                    TemplateRecipeHandler.CachedRecipe cachedRecipe =
                            ((ArrayList<TemplateRecipeHandler.CachedRecipe>) arecipes.get(recipe)).get(index);

                    Field resultField = vatInnerRecipe.getDeclaredField("result");
                    Field inFluidField = vatInnerRecipe.getDeclaredField("inFluid");
                    resultField.setAccessible(true);
                    inFluidField.setAccessible(true);

                    FluidStack result = (FluidStack) resultField.get(cachedRecipe);
                    FluidStack inFluid = (FluidStack) inFluidField.get(cachedRecipe);
                    inputs.add(new Object[] {inFluid});
                    outputs.add(new Object[] {result});
                    resultField.setAccessible(false);
                    inFluidField.setAccessible(false);
                }
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
