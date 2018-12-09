package me.towdium.jecalculation.data;

import me.towdium.jecalculation.JecaCapability;
import me.towdium.jecalculation.JecaConfig;
import me.towdium.jecalculation.JecaItem;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recents;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.Recipes;
import me.towdium.jecalculation.network.packets.PCalculator;
import me.towdium.jecalculation.network.packets.PRecipe;
import me.towdium.jecalculation.network.packets.PRecord;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.JustEnoughCalculation.network;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@Mod.EventBusSubscriber
public class Controller {
    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_RECENTS = "recents";
    public static final String KEY_AMOUNT = "amount";
    static Recipes recipesClient;
    static Recents recentsClient;
    static String amountClient;
    static boolean serverActive = false;

    public static boolean isServerActive() {
        return serverActive;
    }

    public static void setServerActive(boolean serverActive) {
        Controller.serverActive = serverActive;
    }

    static Recipes getRecord() {
        if (serverActive)
            //noinspection ConstantConditions
            return Minecraft.getMinecraft().player.getCapability(JecaCapability.CAPABILITY_RECORD, EnumFacing.UP);
        else return recipesClient;
    }

    static Optional<ItemStack> getStack() {
        InventoryPlayer inv = Minecraft.getMinecraft().player.inventory;
        ItemStack is = inv.getCurrentItem();
        if (is.getItem() instanceof JecaItem) return Optional.of(is);
        is = inv.offHandInventory.get(0);
        return Optional.ofNullable(is.getItem() instanceof JecaItem ? is : null);
    }

    // file, recipes
    public static List<Pair<String, Recipes>> discover() {
        File dir = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/data/");
        File[] fs = dir.listFiles();
        if (fs == null) return new ArrayList<>();
        return Arrays.stream(fs)
                .map(i -> new Pair<>(i.getName(), new Recipes(Utilities.Json.read(i))))
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
        File f = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/data/" + group + ".json");
        Utilities.Json.write(getRecord().serialize(Collections.singleton(group)), f);
        return f;
    }

    public static File export() {
        File f = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/data/groups.json");
        Utilities.Json.write(getRecord().serialize(), f);
        return f;
    }

    public static List<String> getGroups() {
        return getRecord().getGroups();
    }

    public static void addRecipe(String group, Recipe recipe) {
        getRecord().add(group, recipe);
        if (serverActive)
            network.sendToServer(new PRecipe(group, -1, recipe));
    }

    public static void setRecipe(String group, int index, Recipe recipe) {
        getRecord().set(group, index, recipe);
        if (serverActive)
            network.sendToServer(new PRecipe(group, index, recipe));
    }

    public static void removeRecipe(String group, int index) {
        getRecord().remove(group, index);
        if (serverActive)
            network.sendToServer(new PRecipe(group, index, null));
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
        if (!serverActive) return amountClient;
        else return getStack()
                .map(is -> Utilities.getTag(is).getString(KEY_AMOUNT))
                .orElseGet(() -> amountClient);
    }

    public static void setAmount(String amount) {
        if (!serverActive) amountClient = amount;
        else getStack().ifPresent(is -> Utilities.getTag(is).setString(KEY_AMOUNT, amount));
    }

    public static List<ILabel> getRecent() {
        if (!serverActive) return recentsClient.getRecords();
        else {
            ArrayList<ILabel> ret = new ArrayList<>();
            Optional<ItemStack> ois = getStack();
            ois.ifPresent(is -> {
                Recents recent = new Recents(Utilities.getTag(is).getTagList(KEY_RECENTS, 10));
                ret.addAll(recent.getRecords());
            });
            return ret;
        }
    }

    public static void setRecent(ILabel label) {
        if (!serverActive) recentsClient.push(label);
        else {
            Optional<ItemStack> ois = getStack();
            ois.ifPresent(is -> {
                Recents recent = new Recents(Utilities.getTag(is).getTagList(KEY_RECENTS, 10));
                recent.push(label);
                Utilities.getTag(is).setTag(KEY_RECENTS, recent.serialize());
                network.sendToServer(new PCalculator(is));
            });
        }
    }

    public static void loadFromLocal() {
        //noinspection ResultOfMethodCallIgnored
        new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/data").mkdirs();
        File file = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/client.json");
        NBTTagCompound nbt = Utilities.Json.read(file);
        if (nbt != null) {
            recipesClient = nbt.hasKey(KEY_RECIPES) ? new Recipes(nbt.getCompoundTag(KEY_RECIPES)) : new Recipes();
            recentsClient = nbt.hasKey(KEY_RECENTS) ? new Recents(nbt.getTagList(KEY_RECENTS, 10)) : new Recents();
            amountClient = nbt.hasKey(KEY_AMOUNT) ? nbt.getString(KEY_AMOUNT) : "";
            return;
        }
        file = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/default.json");
        nbt = Utilities.Json.read(file);
        recipesClient = nbt == null ? new Recipes() : new Recipes(nbt);
        recentsClient = new Recents();
        amountClient = "";
    }

    public static void writeToLocal() {
        File file = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/record.json");
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag(KEY_RECENTS, recentsClient.serialize());
        nbt.setTag(KEY_RECIPES, recipesClient.serialize());
        nbt.setString(KEY_AMOUNT, amountClient);
        Utilities.Json.write(nbt, file);
    }

    // client side
    @SubscribeEvent
    public static void onLogOut(ClientDisconnectionFromServerEvent event) {
        writeToLocal();
        serverActive = false;
    }

    // server side
    @SubscribeEvent
    public static void onJoin(PlayerLoggedInEvent e) {
        if (!JecaConfig.clientMode)
            network.sendTo(new PRecord(JecaCapability.getRecipes(e.player)), (EntityPlayerMP) e.player);
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof EntityPlayer) {
            e.addCapability(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "recipes"),
                    new JecaCapability.Provider(new Recipes()));
        }
    }

    @SubscribeEvent
    public void onCloneCapability(PlayerEvent.Clone e) {
        Recipes ro = JecaCapability.getRecipes(e.getOriginal());
        JecaCapability.getRecipes(e.getEntityPlayer()).deserialize(ro.serialize());
    }
}
