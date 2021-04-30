package me.towdium.jecalculation.event.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.data.structure.User;
import me.towdium.jecalculation.network.packets.PRecord;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.util.HashMap;
import java.util.UUID;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@ParametersAreNonnullByDefault
public class ControllerServer {
    static HashMap<UUID, User> records = new HashMap<>();

    /**
     * @param uuid   User UUID
     * @param group  Recipe group, crate when not exist
     * @param index  Recipe group, -1 for add
     * @param recipe Recipe content, null for remove
     */
    public static void modify(UUID uuid, String group, int index, @Nullable Recipe recipe) {
        User.Recipes rs = records.get(uuid).recipes;
        if (recipe == null) rs.remove(group, index);
        else if (index == -1) rs.add(group, recipe);
        else rs.set(group, index, recipe);
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        JustEnoughCalculation.network.sendTo(new PRecord(records.get(e.player.getUniqueID())),
                                             (EntityPlayerMP) e.player);
    }

    @SubscribeEvent
    public void onQuit(PlayerEvent.PlayerLoggedOutEvent e) {
        records.remove(e.player.getUniqueID());
    }

    @SubscribeEvent
    public void onLoad(LoadFromFile e) {
        try {
            FileInputStream stream = new FileInputStream(e.getPlayerFile("jeca"));
            NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
            records.put(UUID.fromString(e.playerUUID), new User(tag));
        } catch (FileNotFoundException ex) {
            JustEnoughCalculation.logger.info("Record not found for "
                                              + e.entityPlayer.getDisplayName());
            records.put(UUID.fromString(e.playerUUID), new User());
        } catch (IOException | IllegalArgumentException ex) {
            JustEnoughCalculation.logger.warn("Fail to load records for "
                                              + e.entityPlayer.getDisplayName());
        }
    }

    @SubscribeEvent
    public void onSave(SaveToFile e) {
        try {
            File file = e.getPlayerFile("jeca");
            User user = records.get(UUID.fromString(e.playerUUID));
            NBTTagCompound tag = user.serialize();
            FileOutputStream fos = new FileOutputStream(file);
            CompressedStreamTools.writeCompressed(tag, fos);
        } catch (Exception ex) {
            JustEnoughCalculation.logger.warn("Fail to save records for "
                                              + e.entityPlayer.getDisplayName());
        }
    }
}
