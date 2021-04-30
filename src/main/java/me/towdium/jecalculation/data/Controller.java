package me.towdium.jecalculation.data;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JecaConfig;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recents;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.Recipes;
import me.towdium.jecalculation.utils.wrappers.Triple;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@SideOnly(Side.CLIENT)
public class Controller { // TODO record calculate amount
    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_RECENTS = "recents";
    static Recipes recipesClient;
    static Recents recentsClient;

    static Recipes getRecord() {
        return recipesClient;
    }

    public static List<String> getGroups() {
        Recipes user = getRecord();
        if (user.size() != 0)
            return user.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        else
            return new ArrayList<>();
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

    public static List<Triple<Recipe, String, Integer>> getRecipes() {
        return getRecord().getRecipes();
    }

    public static List<Triple<Recipe, String, Integer>> getRecipes(String group) {
        return getRecord().getRecipes(group);
    }

    public static Optional<Recipe> getRecipe(ILabel label, Recipe.enumIoType type) {
        return getRecord().getRecipe(label, type);
    }

    public static List<ILabel> getRecent() { // TODO reduce usage of it since it reads nbt again
        return recentsClient.getRecords();
    }

    public static void setRecent(ILabel label) {
        recentsClient.push(label);
        writeToLocal();
    }

    public static void loadFromLocal() {
        try {
            File file = JecaConfig.recordFile;
            FileInputStream stream = new FileInputStream(file);
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(stream);
            recipesClient = nbt.hasKey(KEY_RECIPES) ? new Recipes(nbt.getTagList(KEY_RECIPES, 10)) : new Recipes();
            recentsClient = nbt.hasKey(KEY_RECENTS) ? new Recents(nbt.getTagList(KEY_RECENTS, 10)) : new Recents();
        } catch (IOException e) {
            e.printStackTrace();
            recipesClient = new Recipes();
            recentsClient = new Recents();
        }
    }

    public static void writeToLocal() {
        try {
            File file = JecaConfig.recordFile;
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setTag(KEY_RECENTS, recentsClient.serialize());
            nbt.setTag(KEY_RECIPES, recipesClient.serialize());
            FileOutputStream stream = new FileOutputStream(file);
            CompressedStreamTools.writeCompressed(nbt, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
