package pers.towdium.just_enough_calculation.util.helpers;

import net.minecraft.item.ItemStack;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.network.PlayerHandlerSP;
import pers.towdium.just_enough_calculation.network.packets.PacketOredictModify;
import pers.towdium.just_enough_calculation.network.packets.PacketRecordModify;

import java.util.List;

/**
 * Author: Towdium
 * Date:   2016/6/28.
 */
public class PlayerRecordHelper {
    static PlayerHandlerSP playerHandler = ((PlayerHandlerSP) JustEnoughCalculation.proxy.getPlayerHandler());

    public static String getGroupName(int index) {
        return playerHandler.getGroupName(index);
    }

    public static void addRecipe(Recipe recipe, String string) {
        playerHandler.addRecipe(recipe, string);
        JustEnoughCalculation.networkWrapper.sendToServer(new PacketRecordModify(-1, string, "", recipe));
    }

    public static Recipe getRecipe(String string, int index) {
        return playerHandler.getRecipe(string, index);
    }

    public static void removeRecipe(String string, int index) {
        playerHandler.removeRecipe(string, index);
        JustEnoughCalculation.networkWrapper.sendToServer(new PacketRecordModify(index, string, "", null));
    }

    public static void setRecipe(String group, String groupOld, int index, Recipe recipe) {
        playerHandler.setRecipe(group, groupOld, index, recipe);
        JustEnoughCalculation.networkWrapper.sendToServer(new PacketRecordModify(index, group, groupOld, recipe));
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

    public static ItemStack getOreDictPref(List<ItemStack> stacks) {
        return playerHandler.getOreDictPref(stacks);
    }

    public static List<ItemStack> getOreDictPref() {
        return playerHandler.getOreDictPref();
    }

    public static void removeOreDictPref(ItemStack stack) {
        playerHandler.removeOreDictPref(stack);
        JustEnoughCalculation.networkWrapper.sendToServer(new PacketOredictModify(true, stack));
    }

    public static void addOreDictPref(ItemStack stack) {
        playerHandler.addOreDictPref(stack);
        JustEnoughCalculation.networkWrapper.sendToServer(new PacketOredictModify(false, stack));
    }

    public static List<Recipe> getAllRecipeOutput(ItemStack itemStack) {
        return playerHandler.getAllRecipeOutput(itemStack);
    }

    public static void setPlayerHandler(PlayerHandlerSP handler) {
        playerHandler = handler;
    }
}
