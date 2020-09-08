package me.towdium.jecalculation.data;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JecaCapability;
import me.towdium.jecalculation.JecaConfig;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.*;
import me.towdium.jecalculation.network.packets.PCalculator;
import me.towdium.jecalculation.network.packets.PEdit;
import me.towdium.jecalculation.network.packets.PRecord;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.towdium.jecalculation.JustEnoughCalculation.network;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
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

    // file, recipes
    public static List<Pair<String, Recipes>> discover() {
        File dir = new File(FMLPaths.CONFIGDIR.get().toFile(), JustEnoughCalculation.MODID + "/data");
        File[] fs = dir.listFiles();
        Function<File, Recipes> read = f -> {
            CompoundNBT nbt = Utilities.Json.read(f);
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
        recipes.getGroup(group).stream().filter(i -> !hasDuplicate(i)).forEach(buffer::add);
        for (Recipe r : buffer) addRecipe(group, r);
    }

    private static void export(String s, Function<Recipes, CompoundNBT> r) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;

        File f = new File(FMLPaths.CONFIGDIR.get().toFile(), JustEnoughCalculation.MODID + "/data/" + s + ".json");
        player.sendStatusMessage(new TranslationTextComponent(
                "jecalculation.chat.export", f.getAbsolutePath()), false);
    }

    public static void export(String group) {
        export(group, i -> i.serialize(Collections.singleton(group)));
    }

    public static void export() {
        export("groups", Recipes::serialize);
    }

    @Nullable
    public static String getLast() {
        return isServerActive() ? rPlayerServer.last : rPlayerClient.last;
    }

    static void setLast(String last) {
        if (isServerActive()) rPlayerServer.last = last;
        else rPlayerClient.last = last;
    }

    public static List<String> getGroups() {
        return getRecipes().getGroups();
    }

    public static void setRecipe(String neu, String old, int index, Recipe recipe) {
        getRecipes().set(neu, old, index, recipe);
        setLast(neu);
        if (isServerActive()) network.sendToServer(new PEdit(neu, old, index, recipe));
    }

    public static void renameGroup(String old, String neu) {
        getRecipes().renameGroup(old, neu);
        setLast(neu);
        if (isServerActive()) network.sendToServer(new PEdit(neu, old, -1, null));
    }

    public static void addRecipe(String group, Recipe recipe) {
        getRecipes().add(group, recipe);
        setLast(group);
        if (isServerActive()) network.sendToServer(new PEdit(group, null, -1, recipe));
    }

    public static void setRecipe(String group, int index, Recipe recipe) {
        getRecipes().set(group, index, recipe);
        setLast(group);
        if (isServerActive()) network.sendToServer(new PEdit(group, null, index, recipe));
    }

    public static void removeRecipe(String group, int index) {
        getRecipes().remove(group, index);
        setLast(group);
        if (isServerActive()) network.sendToServer(new PEdit(group, null, index, null));
    }

    public static void removeGroup(String group) {
        getRecipes().remove(group);
        setLast(group);
        if (isServerActive()) network.sendToServer(new PEdit(group, null, -1, null));
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

    public static void setRMath(RecordMath math, @Nullable ItemStack is) {
        setR(math, i -> rMathClient = i, KEY_MATH, is);
    }

    public static void setRCraft(RecordCraft rc, @Nullable ItemStack is) {
        setR(rc, i -> rCraftClient = i, KEY_CRAFT, is);
    }

    public static RecordCraft getRCraft(@Nullable ItemStack is) {
        return getR(rCraftClient, KEY_CRAFT, RecordCraft::new, is);
    }

    public static RecordMath getRMath(@Nullable ItemStack is) {
        return getR(rMathClient, KEY_MATH, RecordMath::new, is);
    }

    private static <T extends IRecord> void setR(T t, Consumer<T> c, String s, @Nullable ItemStack is) {
        if (!isServerActive()) c.accept(t);
        else if (is != null) {
            Utilities.getTag(is).put(s, t.serialize());
            network.sendToServer(new PCalculator(is));
        }
    }

    public static <T> T getR(T t, String s, Function<CompoundNBT, T> f, @Nullable ItemStack is) {
        if (!isServerActive()) return t;
        else if (is != null) return f.apply(Utilities.getTag(is).getCompound(s));
        else throw new RuntimeException("Internal error");
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
        return ri.stream().anyMatch(i -> {
            if (ri.getIndex() == index && ri.getGroup().equals(group)) return false;
            else return i.equals(r);
        });
    }

    public static boolean hasDuplicate(Recipe r) {
        return recipeIterator().stream().anyMatch(i -> i.equals(r));
    }

    public static void loadFromLocal() {
        //noinspection ResultOfMethodCallIgnored
        new File(Utilities.config(), "/data").mkdirs();
        File file = new File(Utilities.config() + "/record.json");
        CompoundNBT nbt = Utilities.Json.read(file);
        boolean s = LPlaceholder.state;
        LPlaceholder.state = true;
        if (nbt != null) {
            rCraftClient = new RecordCraft(nbt.getCompound(KEY_CRAFT));
            rMathClient = new RecordMath(nbt.getCompound(KEY_MATH));
            rPlayerClient = nbt.contains(KEY_PLAYER) ? new RecordPlayer(nbt.getCompound(KEY_PLAYER)) : new RecordPlayer();
        } else {
            rPlayerClient = new RecordPlayer();
            rCraftClient = new RecordCraft(new CompoundNBT());
            rMathClient = new RecordMath(new CompoundNBT());
        }
        LPlaceholder.state = s;
    }

    public static void writeToLocal() {
        File file = new File(Utilities.config(), "/record.json");
        CompoundNBT nbt = new CompoundNBT();
        nbt.put(KEY_CRAFT, rCraftClient.serialize());
        nbt.put(KEY_PLAYER, rPlayerClient.serialize());
        nbt.put(KEY_MATH, rMathClient.serialize());
        Utilities.Json.write(nbt, file);
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    static class Client {
        // client side
        @SubscribeEvent
        public static void onLogOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            writeToLocal();
            rPlayerServer = null;
        }
    }

    @Mod.EventBusSubscriber
    static class Server {
        @SubscribeEvent
        public static void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
            if (!JecaConfig.clientMode.get())
                network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.getPlayer()),
                        new PRecord(JecaCapability.getRecord(e.getPlayer())));
        }
    }
}
