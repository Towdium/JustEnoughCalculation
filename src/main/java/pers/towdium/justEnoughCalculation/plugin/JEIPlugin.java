package pers.towdium.justEnoughCalculation.plugin;

import mezz.jei.api.*;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;

import javax.annotation.Nonnull;

/**
 * @author Towdium
 */
@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin{
    public static IJeiRuntime runtime;

    @Override
    public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {

    }

    @Override
    public void onItemRegistryAvailable(IItemRegistry itemRegistry) {

    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new NormalRecipeTransferHandler(VanillaRecipeCategoryUid.CRAFTING));
    }

    @Override
    public void onRecipeRegistryAvailable(@Nonnull IRecipeRegistry recipeRegistry) {

    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        JustEnoughCalculation.log.info("载入了！");
        runtime = jeiRuntime;
    }
}
