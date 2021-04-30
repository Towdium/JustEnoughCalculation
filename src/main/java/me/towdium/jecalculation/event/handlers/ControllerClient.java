package me.towdium.jecalculation.event.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.User;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@SideOnly(Side.CLIENT)
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
    public void onLogOut(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
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
