package me.towdium.jecalculation.data;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JecaConfig;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.*;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;

/**
 * Author: towdium
 * Date: 17-10-15.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class Controller {

    public static final String KEY_MATH = "math";
    public static final String KEY_CRAFT = "craft";
    public static final String KEY_PLAYER = "player";

    static RecordPlayer rPlayerClient;
    static RecordCraft rCraftClient;
    static RecordMath rMathClient;

    static Recipes getRecipes() {
        return rPlayerClient.recipes;
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
            if (nbt == null) {
                JustEnoughCalculation.logger.warn("File " + f.getAbsolutePath() + " contains invalid records.");
                return null;
            } else {
                return new Recipes(nbt);
            }
        };
        return Arrays.stream(fs)
            .map(i -> new Pair<>(i.getName(), read.apply(i)))
            .filter(i -> i.two != null)
            .collect(Collectors.toList());
    }

    public static void inport(Recipes recipes, String group) {
        ArrayList<Recipe> buffer = new ArrayList<>();
        recipes.getGroup(group)
            .stream()
            .filter(i -> !hasDuplicate(i))
            .forEach(buffer::add);
        for (Recipe r : buffer) addRecipe(group, r);
    }

    private static void export(String s, Function<Recipes, NBTTagCompound> r) {
        EntityClientPlayerMP player = Utilities.getPlayer();
        if (player == null) return;

        File f = JecaConfig.getDataFile(s);
        Utilities.Json.write(r.apply(getRecipes()), f);
        player.addChatMessage(new ChatComponentTranslation("jecalculation.chat.export", f.getAbsolutePath()));
    }

    public static void export(String group) {
        export(group, i -> i.serialize(Collections.singleton(group)));
    }

    public static void export() {
        export("groups", Recipes::serialize);
    }

    @Nullable
    public static String getLast() {
        return rPlayerClient.last;
    }

    static void setLast(String last) {
        rPlayerClient.last = last;
    }

    public static List<String> getGroups() {
        return getRecipes().getGroups();
    }

    public static void setRecipe(String neu, String old, int index, Recipe recipe) {
        getRecipes().set(neu, old, index, recipe);
        setLast(neu);
        writeToLocal();
    }

    public static void renameGroup(String old, String neu) {
        getRecipes().renameGroup(old, neu);
        setLast(neu);
        writeToLocal();
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

    public static void removeGroup(String group) {
        getRecipes().remove(group);
        setLast(group);
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
        setR(math, i -> rMathClient = i, KEY_MATH);
    }

    public static void setRCraft(RecordCraft rc) {
        setR(rc, i -> rCraftClient = i, KEY_CRAFT);
    }

    public static RecordCraft getRCraft() {
        return getR(rCraftClient, KEY_CRAFT, RecordCraft::new);
    }

    public static RecordMath getRMath() {
        return getR(rMathClient, KEY_MATH, RecordMath::new);
    }

    private static <T extends IRecord> void setR(T t, Consumer<T> c, String s) {
        c.accept(t);
    }

    public static <T> T getR(T t, String s, Function<NBTTagCompound, T> f) {
        return t;
    }

    /**
     * Checks if recipe has duplications, except the recipe at group->index
     *
     * @param r     Recipe to check
     * @param group Group exclusive
     * @param index Index exclusive
     * @return if duplicate found
     */
    public static boolean hasDuplicate(Recipe r, String group, int index) {
        Recipes.RecipeIterator ri = recipeIterator();
        return ri.stream()
            .anyMatch(i -> {
                if (ri.getIndex() == index && ri.getGroup()
                    .equals(group)) return false;
                else return i.equals(r);
            });
    }

    public static boolean hasDuplicate(Recipe r) {
        return recipeIterator().stream()
            .anyMatch(i -> i.equals(r));
    }

    public static void loadFromLocal() {
        // noinspection ResultOfMethodCallIgnored
        JecaConfig.dataDir.mkdirs();
        File file = JecaConfig.recordFile;
        NBTTagCompound nbt = Utilities.Json.read(file);
        boolean s = LPlaceholder.state;
        LPlaceholder.state = true;
        if (nbt != null) {
            rCraftClient = new RecordCraft(nbt.getCompoundTag(KEY_CRAFT));
            rMathClient = new RecordMath(nbt.getCompoundTag(KEY_MATH));
            rPlayerClient = nbt.hasKey(KEY_PLAYER) ? new RecordPlayer(nbt.getCompoundTag(KEY_PLAYER))
                : new RecordPlayer();
        } else {
            rPlayerClient = new RecordPlayer();
            rCraftClient = new RecordCraft(new NBTTagCompound());
            rMathClient = new RecordMath(new NBTTagCompound());
        }
        LPlaceholder.state = s;
    }

    public static void writeToLocal() {
        File file = JecaConfig.recordFile;
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag(KEY_CRAFT, rCraftClient.serialize());
        nbt.setTag(KEY_PLAYER, rPlayerClient.serialize());
        nbt.setTag(KEY_MATH, rMathClient.serialize());
        Utilities.Json.write(nbt, file);
    }
}
