package me.towdium.jecalculation.data;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JecaCapability;
import me.towdium.jecalculation.JecaConfig;
import me.towdium.jecalculation.JecaItem;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.structure.*;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiCraft;
import me.towdium.jecalculation.gui.guis.GuiMath;
import me.towdium.jecalculation.network.packets.PCalculator;
import me.towdium.jecalculation.network.packets.PEdit;
import me.towdium.jecalculation.network.packets.PRecord;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.towdium.jecalculation.JustEnoughCalculation.network;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@Mod.EventBusSubscriber
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Controller {
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
        if (isServerActive()) return rPlayerServer.recipes;
        else return rPlayerClient.recipes;
    }

    public static Optional<ItemStack> getStack() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ItemStack is = player.getHeldItem(EnumHand.MAIN_HAND);
        if (is.getItem() instanceof JecaItem) return Optional.of(is);
        is = player.getHeldItem(EnumHand.OFF_HAND);
        return Optional.ofNullable(is.getItem() instanceof JecaItem ? is : null);
    }

    // file, recipes
    public static List<Pair<String, Recipes>> discover() {
        File dir = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/data/");
        File[] fs = dir.listFiles();
        Function<File, Recipes> read = f -> {
            NBTTagCompound nbt = Utilities.Json.read(f);
            JustEnoughCalculation.logger.warn("File " + f.getAbsolutePath() + " contains invalid records.");
            return nbt == null ? null : new Recipes(nbt);
        };
        if (fs == null) return new ArrayList<>();
        return Arrays.stream(fs)
                .map(i -> new Pair<>(i.getName(), read.apply(i)))
                .filter(i -> i.two != null)
                .collect(Collectors.toList());
    }

    public static void inport(Recipes recipes, String group) {
        ArrayList<Recipe> buffer = new ArrayList<>();
        recipes.getGroup(group).forEach(i ->
                getRecipes().getGroup(group).stream().filter(j -> j.equals(i)).findAny().orElseGet(() -> {
                    buffer.add(i);
                    return null;
                }));
        for (Recipe r : buffer) addRecipe(group, r);
    }

    public static File export(String group) {
        File f = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/data/" + group + ".json");
        Utilities.Json.write(getRecipes().serialize(Collections.singleton(group)), f);
        return f;
    }

    public static File export() {
        File f = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/data/groups.json");
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
        if (isServerActive()) network.sendToServer(new PEdit(group, -1, recipe));
    }

    public static void setRecipe(String group, int index, Recipe recipe) {
        getRecipes().set(group, index, recipe);
        setLast(group);
        if (isServerActive()) network.sendToServer(new PEdit(group, index, recipe));
    }

    public static void removeRecipe(String group, int index) {
        getRecipes().remove(group, index);
        setLast(group);
        if (isServerActive()) network.sendToServer(new PEdit(group, index, null));
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
        if (!isServerActive()) rMathClient = math;
        else {
            Optional<ItemStack> ois = getStack();
            ois.ifPresent(is -> {
                Utilities.getTag(is).setTag(KEY_MATH, math.serialize());
                network.sendToServer(new PCalculator(is));
            });
        }
    }

    public static RecordCraft getRCraft() {
        if (!isServerActive()) return rCraftClient;
        else return new RecordCraft(getStack()
                .map(i -> Utilities.getTag(i).getCompoundTag(KEY_CRAFT))
                .orElse(new NBTTagCompound()));
    }

    public static void setRCraft(RecordCraft rc) {
        if (!isServerActive()) rCraftClient = rc;
        else {
            Optional<ItemStack> ois = getStack();
            ois.ifPresent(is -> {
                Utilities.getTag(is).setTag(KEY_CRAFT, rc.serialize());
                network.sendToServer(new PCalculator(is));
            });
        }
    }

    public static RecordMath getRMath() {
        if (!isServerActive()) return rMathClient;
        else return new RecordMath(getStack()
                .map(i -> Utilities.getTag(i).getCompoundTag(KEY_MATH))
                .orElse(new NBTTagCompound()));
    }

    public static boolean hasDuplicate(Recipe r) {
        return getRecipes().hasDuplicate(r);
    }

    public static void loadFromLocal() {
        //noinspection ResultOfMethodCallIgnored
        new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/data").mkdirs();
        File file = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/record.json");
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
        File file = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/record.json");
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag(KEY_CRAFT, rCraftClient.serialize());
        nbt.setTag(KEY_PLAYER, rPlayerClient.serialize());
        nbt.setTag(KEY_MATH, rMathClient.serialize());
        Utilities.Json.write(nbt, file);
    }

    public static void openGuiCraft() {
        if (!Controller.isServerActive()) JecaGui.displayGui(true, true, new GuiCraft());
        else Minecraft.getMinecraft().player.sendMessage(
                new TextComponentTranslation("jecalculation.chat.server_mode"));
    }

    public static void openGuiMath() {
        if (!Controller.isServerActive()) JecaGui.displayGui(true, true, new GuiMath());
        else Minecraft.getMinecraft().player.sendMessage(
                new TextComponentTranslation("jecalculation.chat.server_mode"));
    }

    // client side
    @SubscribeEvent
    public static void onLogOut(ClientDisconnectionFromServerEvent event) {
        writeToLocal();
        rPlayerServer = null;
    }

    // server side
    @SubscribeEvent
    public static void onJoin(PlayerLoggedInEvent e) {
        if (!JecaConfig.clientMode)
            network.sendTo(new PRecord(JecaCapability.getRecord(e.player)), (EntityPlayerMP) e.player);
    }
}
