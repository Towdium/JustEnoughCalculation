package me.towdium.jecalculation.data;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.User;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber
public class ControllerClient {
    static User recordWorld;
    static User recordClient;

    static User getRecord() {
        return recordWorld == null ? recordClient : recordWorld;
    }

    public static void addRecipe(String group, Recipe recipe) {
        getRecord().recipes.add(group, recipe);
        // TODO sync
    }

    public static void setRecipe(String group, int index, Recipe recipe) {
        getRecord().recipes.set(group, index, recipe);
        // TODO sync
    }

    public static void removeRecipe(String group, int index) {
        getRecord().recipes.remove(group, index);
        // TODO sync
    }

    /**
     * This is used when syncing data from the server.
     * Client record will be initialized when client starts.
     *
     * @param nbt {@link NBTTagCompound containing the user data}
     */
    public static void init(@Nullable NBTTagCompound nbt) {
        recordWorld = nbt == null ? new User() : new User(nbt);
    }

    @SubscribeEvent
    public static void onLogOut(ClientDisconnectionFromServerEvent event) {
        recordWorld = null;
    }

    public static List<Recipe> search(String group, ILabel label, Recipe.enumIoType type) {
        return getRecord().recipes.search(group, label, type);
    }

    public static User.Recent getRecent() {
        return getRecord().recent;
    }

    public static void setRecent(ILabel label) {
        getRecord().recent.push(label);  // no need to sync because client only
    }
}
