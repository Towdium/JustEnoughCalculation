package me.towdium.jecalculation.data;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.capacity.JecaCapability;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recents;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.Recipes;
import me.towdium.jecalculation.network.packets.PCalculator;
import me.towdium.jecalculation.network.packets.PRecipe;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Triple;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
@Mod.EventBusSubscriber(Side.CLIENT)
public class ControllerClient {
    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_RECENTS = "recents";
    static Recipes recipesClient;
    static Recents recentsClient;

    static Recipes getRecord() {
        //noinspection ConstantConditions
        return Minecraft.getMinecraft().player.getCapability(JecaCapability.CAPABILITY_RECORD, EnumFacing.UP);
    }

    public static List<String> getGroups() {
        Recipes user = getRecord();
        if (user.size() != 0) return user.stream()
                .map(Map.Entry::getKey).collect(Collectors.toList());
        else return new ArrayList<>();
    }

    public static void addRecipe(String group, Recipe recipe) {
        getRecord().add(group, recipe);
        if (JustEnoughCalculation.side == JustEnoughCalculation.enumSide.BOTH)
            JustEnoughCalculation.network.sendToServer(new PRecipe(group, -1, recipe));
    }

    public static void setRecipe(String group, int index, Recipe recipe) {
        getRecord().set(group, index, recipe);
        if (JustEnoughCalculation.side == JustEnoughCalculation.enumSide.BOTH)
            JustEnoughCalculation.network.sendToServer(new PRecipe(group, index, recipe));
    }

    public static void removeRecipe(String group, int index) {
        getRecord().remove(group, index);
        if (JustEnoughCalculation.side == JustEnoughCalculation.enumSide.BOTH)
            JustEnoughCalculation.network.sendToServer(new PRecipe(group, index, null));
    }

    @SubscribeEvent
    public static void onLogOut(ClientDisconnectionFromServerEvent event) {
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

    public static List<Triple<Recipe, String, Integer>> getRecipes(ILabel label, Recipe.enumIoType type) {
        return getRecord().getRecipes(label, type);
    }

    public static List<ILabel> getRecent() {
        if (JustEnoughCalculation.side == JustEnoughCalculation.enumSide.CLIENT)
            return recentsClient.getRecords();
        else {
            ArrayList<ILabel> ret = new ArrayList<>();
            Optional<ItemStack> ois = Utilities.getStack();
            ois.ifPresent(is -> {
                Recents recent = new Recents(Utilities.getTag(is).getTagList(Recents.IDENTIFIER, 10));
                ret.addAll(recent.getRecords());
            });
            return ret;
        }
    }

    public static void setRecent(ILabel label) {
        if (JustEnoughCalculation.side == JustEnoughCalculation.enumSide.CLIENT)
            recentsClient.push(label);
        else {
            Optional<ItemStack> ois = Utilities.getStack();
            ois.ifPresent(is -> {
                Recents recent = new Recents(Utilities.getTag(is).getTagList(Recents.IDENTIFIER, 10));
                recent.push(label);
                Utilities.getTag(is).setTag(Recents.IDENTIFIER, recent.serialize());
                JustEnoughCalculation.network.sendToServer(new PCalculator(is));
            });
        }
    }

    public static void loadFromLocal() {
        try {
            File file = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/record.dat");
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
            File file = new File(Loader.instance().getConfigDir(), "JustEnoughCalculation/record.dat");
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
