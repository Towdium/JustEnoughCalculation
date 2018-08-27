package me.towdium.jecalculation.data;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.capacity.JecaCapacityProvider;
import me.towdium.jecalculation.data.structure.Recipes;
import me.towdium.jecalculation.network.packets.PRecord;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ControllerServer {

//    /**
//     * @param uuid   User UUID
//     * @param group  Recipe group, crate when not exist
//     * @param index  Recipe group, -1 for add
//     * @param recipe Recipe content, null for remove
//     */
//    public static void modify(UUID uuid, String group, int index, @Nullable Recipe recipe) {
//        User.Recipes rs = records.get(uuid).recipes;
//        if (recipe == null) rs.remove(group, index);
//        else if (index == -1) rs.add(group, recipe);
//        else rs.set(group, index, recipe);
//    }

    @SubscribeEvent
    public static void onJoin(PlayerLoggedInEvent e) {
        JustEnoughCalculation.network.sendTo(new PRecord(Utilities.getRecipes(e.player)), (EntityPlayerMP) e.player);
    }

//    @SubscribeEvent
//    public static void onQuit(PlayerLoggedOutEvent e) {
//        records.remove(e.player.getUniqueID());
//    }

//    @SubscribeEvent
//    public static void onLoad(LoadFromFile e) {
//        try {
//            FileInputStream stream = new FileInputStream(e.getPlayerFile("jeca"));
//            NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
//            records.put(UUID.fromString(e.getPlayerUUID()), new User(tag));
//        } catch (FileNotFoundException ex) {
//            JustEnoughCalculation.logger.info("Record not found for "
//                    + e.getEntityPlayer().getDisplayNameString());
//            records.put(UUID.fromString(e.getPlayerUUID()), new User());
//        } catch (IOException | IllegalArgumentException ex) {
//            JustEnoughCalculation.logger.warn("Fail to load records for "
//                    + e.getEntityPlayer().getDisplayNameString());
//        }
//    }

//    @SubscribeEvent
//    public static void onSave(SaveToFile e) {
//        try {
//            File file = e.getPlayerFile("jeca");
//            User user = records.get(UUID.fromString(e.getPlayerUUID()));
//            NBTTagCompound tag = user.serialize();
//            FileOutputStream fos = new FileOutputStream(file);
//            CompressedStreamTools.writeCompressed(tag, fos);
//        } catch (Exception ex) {
//            JustEnoughCalculation.logger.warn("Fail to save records for "
//                    + e.getEntityPlayer().getDisplayNameString());
//        }
//    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof EntityPlayer) {
            e.addCapability(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "record"),
                    new JecaCapacityProvider(new Recipes()));
        }
    }

    @SubscribeEvent
    public void cloneCapabilitiesEvent(PlayerEvent.Clone e) {
        Recipes ro = Utilities.getRecipes(e.getOriginal());
        Utilities.getRecipes(e.getEntityPlayer()).deserialize(ro.serialize());
    }
}
