package pers.towdium.justEnoughCalculation.plugin;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Towdium
 */
@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin{
    public static IJeiRuntime runtime;
    public static IRecipeRegistry recipeRegistry;


    @Override
    public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {
    }

    @Override
    public void onItemRegistryAvailable(IItemRegistry itemRegistry) {
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        String[] blackList = JustEnoughCalculation.JECConfig.EnumItems.ListRecipeBlackList.getProperty().getStringList();
        LOOP:
        for(String s : JustEnoughCalculation.JECConfig.EnumItems.ListRecipeCategory.getProperty().getStringList()){
            for(String black : blackList){
                if(black.equals(s)){
                    continue LOOP;
                }
            }
            registry.getRecipeTransferRegistry().addRecipeTransferHandler(new NormalRecipeTransferHandler(s));
        }
    }

    @Override
    public void onRecipeRegistryAvailable(@Nonnull IRecipeRegistry recipeRegistry) {
        JEIPlugin.recipeRegistry = recipeRegistry;
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        Set<String> identifiers = new HashSet<>();
        runtime = jeiRuntime;
        identifiers.addAll(ImmutableList.copyOf(JustEnoughCalculation.JECConfig.EnumItems.ListRecipeCategory.getProperty().getStringList()));
        for(IRecipeCategory category : recipeRegistry.getRecipeCategories()){
            identifiers.add(category.getUid());
        }
        int size = identifiers.size();
        String[] buffer = new String[size];
        for (String s : identifiers){
            buffer[--size] = s;
        }
        JustEnoughCalculation.JECConfig.EnumItems.ListRecipeCategory.getProperty().set(buffer);
        JustEnoughCalculation.JECConfig.save();
    }
}
