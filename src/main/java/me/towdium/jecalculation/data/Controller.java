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
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    static Recipes recipesClient;
    static Recents recentsClient;
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
        if (fs == null) return new ArrayList<>();
        Function<File, Recipes> read = f -> {
            NBTTagCompound nbt = Utilities.Json.read(f);
            JustEnoughCalculation.logger.warn("File " + f.getAbsolutePath() + " contains invalid records.");
            return nbt == null ? null : new Recipes(nbt);
        };
        return Arrays.stream(fs)
                     .map(i -> new Pair<>(i.getName(), read.apply(i)))
                     .filter(i -> i.two != null)
                     .collect(Collectors.toList());
    }

    public static void inport(Recipes recipes, String group) {
        ArrayList<Recipe> buffer = new ArrayList<>();
        recipes.flatStream(group).forEach(i ->
                                                  getRecord().flatStream(group).filter(j -> j.one.equals(i.one)).findAny().orElseGet(() -> {
                                                      buffer.add(i.one);
                                                      return null;
                                                  }));
        for (Recipe r : buffer) addRecipe(group, r);
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

    public static List<Trio<Recipe, String, Integer>> getRecipes() {
        return getRecord().getRecipes();
    }

    public static List<Trio<Recipe, String, Integer>> getRecipes(String group) {
        return getRecord().getRecipes(group);
    }

    public static Optional<Recipe> getRecipe(ILabel label) {
        return getRecord().getRecipe(label);
    }

    public static String getAmount() {
        return amountClient;
    }

    public static void setAmount(String amount) {
        amountClient = amount;
    }


    public static List<ILabel> getRecent() {
        return recentsClient.getRecords();
    }

    public static void setRecent(ILabel label) {
        recentsClient.push(label);
        writeToLocal();
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
            return;
        }
        file = JecaConfig.defaultFile;
        nbt = Utilities.Json.read(file);
        recipesClient = nbt == null ? new Recipes() : new Recipes(nbt);
        recentsClient = new Recents();
        amountClient = "";
    }

    public static void writeToLocal() {
        File file = JecaConfig.recordFile;
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag(KEY_RECENTS, recentsClient.serialize());
        nbt.setTag(KEY_RECIPES, recipesClient.serialize());
        nbt.setString(KEY_AMOUNT, amountClient);
        Utilities.Json.write(nbt, file);
    }
}
