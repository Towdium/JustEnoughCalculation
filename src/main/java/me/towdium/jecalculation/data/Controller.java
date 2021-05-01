package me.towdium.jecalculation.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JecaConfig;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.structure.*;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiCraft;
import me.towdium.jecalculation.gui.guis.GuiMath;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;

import javax.annotation.Nullable;
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
    public static final String KEY_MATH = "math";
    public static final String KEY_CRAFT = "craft";
    public static final String KEY_PLAYER = "player";

    static RecordPlayer rPlayerServer;
    static RecordPlayer rPlayerClient;
    static RecordCraft rCraftClient;
    static RecordMath rMathClient;

    public static void setRecordsServer(RecordPlayer r) {
        rPlayerServer = r;
    }

    public static boolean isServerActive() {
        return rPlayerServer != null;
    }

    static Recipes getRecipes() {
        if (isServerActive())
            return rPlayerServer.recipes;
        else
            return rPlayerClient.recipes;
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
               .forEach(i -> getRecipes().getGroup(group).stream().filter(j -> j.equals(i)).findAny().orElseGet(() -> {
                   buffer.add(i);
                   return null;
               }));
        for (Recipe r : buffer)
            addRecipe(group, r);
    }

    public static File export(String group) {
        File f = JecaConfig.getDataFile(group);
        Utilities.Json.write(getRecipes().serialize(Collections.singleton(group)), f);
        return f;
    }

    public static File export() {
        File f = JecaConfig.getDataFile("groups");
        Utilities.Json.write(getRecipes().serialize(), f);
        return f;
    }

    @Nullable
    public static String getLast() {
        return isServerActive() ? rPlayerServer.last : rPlayerClient.last;
    }

    static void setLast(String last) {
        if (isServerActive()) rPlayerServer.last = last;
        else rPlayerServer.last = last;
    }

    public static List<String> getGroups() {
        return getRecipes().getGroups();
    }

    public static void addRecipe(String group, Recipe recipe) {
        getRecipes().add(group, recipe);
        setLast(group);
        writeToLocal();
    }

    public static void setRecipe(String group, int index, Recipe recipe) {
        getRecipes().set(group, index, recipe);
        setLast(group);
        writeToLocal();
    }

    public static void removeRecipe(String group, int index) {
        getRecipes().remove(group, index);
        setLast(group);
        writeToLocal();
    }

    public static Recipe getRecipe(String group, int index) {
        return getRecipes().getRecipe(group, index);
    }

    public static Stream<Pair<String, List<Recipe>>> stream() {
        return getRecipes().stream();
    }

    public static Recipes.RecipeIterator recipeIterator() {
        return getRecipes().recipeIterator();
    }

    public static Recipes.RecipeIterator recipeIterator(String group) {
        return getRecipes().recipeIterator(group);
    }

    public static void setRMath(RecordMath math) {
        rMathClient = math;
    }

    public static RecordCraft getRCraft() {
        return rCraftClient;

    }

    public static void setRCraft(RecordCraft rc) {
        rCraftClient = rc;

    }

    public static RecordMath getRMath() {
        return rMathClient;
    }


    public static boolean hasDuplicate(Recipe r) {
        return getRecipes().hasDuplicate(r);
    }

    public static void loadFromLocal() {
        //noinspection ResultOfMethodCallIgnored
        JecaConfig.dataDir.mkdirs();
        File file = JecaConfig.recordFile;
        NBTTagCompound nbt = Utilities.Json.read(file);
        if (nbt != null) {
            rCraftClient = new RecordCraft(nbt.getCompoundTag(KEY_CRAFT));
            rMathClient = new RecordMath(nbt.getCompoundTag(KEY_MATH));
            rPlayerClient = nbt.hasKey(KEY_PLAYER) ? new RecordPlayer(nbt.getCompoundTag(KEY_PLAYER)) : new RecordPlayer();
        } else {
            rPlayerClient = new RecordPlayer();
            rCraftClient = new RecordCraft(new NBTTagCompound());
            rMathClient = new RecordMath(new NBTTagCompound());
        }
    }

    public static void writeToLocal() {
        File file = JecaConfig.recordFile;
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag(KEY_CRAFT, rCraftClient.serialize());
        nbt.setTag(KEY_PLAYER, rPlayerClient.serialize());
        nbt.setTag(KEY_MATH, rMathClient.serialize());
        Utilities.Json.write(nbt, file);
    }

    public static void openGuiCraft() {
        if (!Controller.isServerActive()) JecaGui.displayGui(true, true, new GuiCraft());
        else Minecraft.getMinecraft().thePlayer.addChatMessage(
                new ChatComponentTranslation("jecalculation.chat.server_mode"));
    }

    public static void openGuiMath() {
        if (!Controller.isServerActive()) JecaGui.displayGui(true, true, new GuiMath());
        else Minecraft.getMinecraft().thePlayer.addChatMessage(
                new ChatComponentTranslation("jecalculation.chat.server_mode"));
    }

}
