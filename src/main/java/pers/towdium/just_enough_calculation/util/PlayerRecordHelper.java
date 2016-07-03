package pers.towdium.just_enough_calculation.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pers.towdium.just_enough_calculation.core.PlayerHandlerSP;
import pers.towdium.just_enough_calculation.core.Recipe;

import java.util.List;

/**
 * Author: Towdium
 * Date:   2016/6/28.
 */
@SideOnly(Side.CLIENT)
public class PlayerRecordHelper {
    static PlayerHandlerSP playerHandler = new PlayerHandlerSP();

    public static String getGroupName(int index) {
        return playerHandler.getGroupName(index);
    }

    public static void addRecipe(Recipe recipe, String string) {
        playerHandler.addRecipe(recipe, string);
    }

    public static Recipe getRecipe(String string, int index) {
        return playerHandler.getRecipe(string, index);
    }

    public static void removeRecipe(String string, int index) {
        playerHandler.removeRecipe(string, index);
    }

    public static void setRecipe(String group, String groupOld, int index, Recipe recipe) {
        playerHandler.setRecipe(group, groupOld, index, recipe);
    }

    public static Recipe getRecipeOutput(ItemStack itemStack) {
        return playerHandler.getRecipeOutput(itemStack);
    }

    public static List<Recipe> getRecipeInGroup(String string) {
        return playerHandler.getRecipeInGroup(string);
    }

    public static int getSizeRecipe() {
        return playerHandler.getSizeRecipe();
    }

    public static int getSizeGroup() {
        return playerHandler.getSizeGroup();
    }

    public static int getIndexGroup(String s) {
        return playerHandler.getIndexGroup(s);
    }
}
