package me.towdium.jecalculation.nei.adapter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.common.registry.GameData;

@ParametersAreNonnullByDefault
public class Thaum implements IAdapter {

    @Override
    public Set<String> getAllOverlayIdentifier() {
        return new HashSet<>(
            Arrays.asList(
                "arcaneshapedrecipes",
                "arcaneshapelessrecipes",
                "aspectsRecipe",
                "cruciblerecipe",
                "infusionCrafting"));
    }

    private static final Set<Class<?>> defaultHandlers;

    static {
        List<String> handlers = Stream
            .of(
                "ArcaneShapedRecipeHandler",
                "ArcaneShapelessRecipeHandler",
                "AspectRecipeHandler",
                "CrucibleRecipeHandler",
                "InfusionRecipeHandler")
            .map(name -> "com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler." + name)
            .collect(Collectors.toList());
        defaultHandlers = new HashSet<>();
        for (String handler : handlers) {
            try {
                defaultHandlers.add(Class.forName(handler));
            } catch (ClassNotFoundException ignored) {

            }
        }
    }

    @Override
    public void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        if (defaultHandlers.stream()
            .anyMatch(handler -> handler.isInstance(recipe))) {
            // remove all aspects
            Iterator<Object[]> it = inputs.iterator();
            while (it.hasNext()) {
                Object[] items = it.next();
                for (Object item : items) {
                    if (item instanceof ItemStack) {
                        String name = GameData.getItemRegistry()
                            .getNameForObject(((ItemStack) item).getItem());
                        if ("thaumcraftneiplugin:Aspect".equals(name)) {
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }
    }
}
