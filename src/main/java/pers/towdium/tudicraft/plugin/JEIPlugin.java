package pers.towdium.tudicraft.plugin;

import mezz.jei.JeiRuntime;
import mezz.jei.api.*;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.transfer.BasicRecipeTransferHandler;
import pers.towdium.tudicraft.Tudicraft;

import javax.annotation.Nonnull;
import javax.swing.plaf.TableUI;

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
        Tudicraft.log.info("载入了！");
        runtime = jeiRuntime;
    }
}
