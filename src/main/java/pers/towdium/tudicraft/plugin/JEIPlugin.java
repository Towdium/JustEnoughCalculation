package pers.towdium.tudicraft.plugin;

import mezz.jei.JeiRuntime;
import mezz.jei.api.*;
import pers.towdium.tudicraft.Tudicraft;

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
    public void register(IModRegistry registry) {

    }

    @Override
    public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {

    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        Tudicraft.log.info("载入了！");
        runtime = jeiRuntime;
    }
}
