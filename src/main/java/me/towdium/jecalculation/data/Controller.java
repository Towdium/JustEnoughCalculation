package me.towdium.jecalculation.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JecaConfig;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recents;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.Recipes;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class Controller {
    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_RECENTS = "recents";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_INVENTORY = "inventory";
    static Recipes recipesClient;
    static Recents recentsClient;
    static boolean inventoryClient;
    static String amountClient;

    static Recipes getRecord() {
        return recipesClient;
    }

    static Optional<ItemStack> getStack() {
        return Optional.empty();
    }

    // file, recipes
    public static List<Pair<String, Recipes>> discover() {
        File dir = JecaConfig.dataDir;
        File[] fs = dir.listFiles();
        if (fs == null)
            return new ArrayList<>();
        Function<File, Recipes> read = f -> {
            NBTTagCompound nbt = Utilities.Json.read(f);
            JustEnoughCalculation.logger.warn("File " + f.getAbsolutePath() + " contains invalid records.");
            return nbt == null ? null : new Recipes(nbt);
        };
        return Arrays.stream(fs).map(i -> new Pair<>(i.getName(), read.apply(i))).filter(i -> i.two != null)
                     .collect(Collectors.toList());
    }

    public static void inport(Recipes recipes, String group) {
        ArrayList<Recipe> buffer = new ArrayList<>();
        recipes.getGroup(group)
               .forEach(i -> getRecord().getGroup(group).stream().filter(j -> j.equals(i)).findAny().orElseGet(() -> {
                   buffer.add(i);
                   return null;
               }));
        for (Recipe r : buffer)
            addRecipe(group, r);
    }

    public static File export(String group) {
        File f = JecaConfig.getDataFile(group);
        Utilities.Json.write(getRecord().serialize(Collections.singleton(group)), f);
        return f;
    }

    public static File export() {
        File f = JecaConfig.getDataFile("groups");
        Utilities.Json.write(getRecord().serialize(), f);
        return f;
    }

    public static List<String> getGroups() {
        return getRecord().getGroups();
    }

    public static void addRecipe(String group, Recipe recipe) {
        getRecord().add(group, recipe);
        writeToLocal();
    }

    public static void setRecipe(String group, int index, Recipe recipe) {
        getRecord().set(group, index, recipe);
        writeToLocal();
    }

    public static void removeRecipe(String group, int index) {
        getRecord().remove(group, index);
        writeToLocal();
    }

    public static Recipe getRecipe(String group, int index) {
        return getRecord().getRecipe(group, index);
    }

    public static Stream<Pair<String, List<Recipe>>> stream() {
        return getRecord().stream();
    }

    public static Recipes.RecipeIterator recipeIterator() {
        return getRecord().recipeIterator();
    }

    public static List<Recipe> getRecipes() {
        return getRecord().getRecipes();
    }

    public static List<Recipe> getRecipes(String group) {
        return getRecord().getRecipes(group);
    }

    public static String getAmount() {
        return amountClient;
    }

    public static void setAmount(String amount) {
        amountClient = amount;
    }

    public static boolean getDetectInv() {
        return inventoryClient;
    }

    public static void setDetectInv(boolean b) {
        inventoryClient = b;
    }

    public static List<ILabel> getRecent() {
        return recentsClient.getRecords();
    }

    public static void setRecent(ILabel label, boolean replace) {
        recentsClient.push(label, replace);
        writeToLocal();
    }

    public static boolean hasDuplicate(Recipe r) {
        return getRecord().hasDuplicate(r);
    }

    public static void loadFromLocal() {
        //noinspection ResultOfMethodCallIgnored
        JecaConfig.dataDir.mkdirs();
        File file = JecaConfig.recordFile;
        NBTTagCompound nbt = Utilities.Json.read(file);
        if (nbt != null) {
            recipesClient = nbt.hasKey(KEY_RECIPES) ? new Recipes(nbt.getCompoundTag(KEY_RECIPES)) : new Recipes();
            recentsClient = nbt.hasKey(KEY_RECENTS) ? new Recents(nbt.getTagList(KEY_RECENTS, 10)) : new Recents();
            amountClient = nbt.hasKey(KEY_AMOUNT) ? nbt.getString(KEY_AMOUNT) : "";
            inventoryClient = !nbt.hasKey(KEY_INVENTORY) || nbt.getBoolean(KEY_INVENTORY);
        } else {
            recipesClient = new Recipes();
            recentsClient = new Recents();
            amountClient = "";
            inventoryClient = true;
        }
    }

    public static void writeToLocal() {
        File file = JecaConfig.recordFile;
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag(KEY_RECENTS, recentsClient.serialize());
        nbt.setTag(KEY_RECIPES, recipesClient.serialize());
        nbt.setString(KEY_AMOUNT, amountClient);
        nbt.setBoolean(KEY_INVENTORY, inventoryClient);
        Utilities.Json.write(nbt, file);
    }
}
