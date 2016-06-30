package pers.towdium.just_enough_calculation.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.core.PlayerHandlerSP;

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

    public static void addRecipe(Recipe recipe) {
        playerHandler.addRecipe(recipe);
    }

    public static Recipe getRecipe(int index) {
        return playerHandler.getRecipe(index);
    }

    public static void removeRecipe(int index) {
        playerHandler.removeRecipe(index);
    }

    public static Recipe getRecipeOutput(ItemStack itemStack) {
        return playerHandler.getRecipeOutput(itemStack);
    }

    public static List<Integer> getIndexOutput(ItemStack itemStack) {
        return playerHandler.getIndexOutput(itemStack);
    }

    public static List<Integer> getIndexCatalyst(ItemStack itemStack) {
        return playerHandler.getIndexCatalyst(itemStack);
    }

    public static List<Integer> getIndexInput(ItemStack itemStack) {
        return playerHandler.getIndexInput(itemStack);
    }

    public static int getSize() {
        return playerHandler.getSize();
    }
}
