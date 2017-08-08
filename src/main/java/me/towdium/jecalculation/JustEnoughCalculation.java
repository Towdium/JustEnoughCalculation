package me.towdium.jecalculation;

import me.towdium.jecalculation.item.ItemCalculator;
import me.towdium.jecalculation.item.ItemFluidContainer;
import me.towdium.jecalculation.item.ItemLabel;
import me.towdium.jecalculation.network.IProxy;
import me.towdium.jecalculation.network.packets.PacketOredictModify;
import me.towdium.jecalculation.network.packets.PacketRecordModify;
import me.towdium.jecalculation.network.packets.PacketRecordSync;
import me.towdium.jecalculation.network.packets.PacketSyncCalculator;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Towdium
 */
@Mod(modid = JustEnoughCalculation.Reference.MODID, name = JustEnoughCalculation.Reference.MODNAME, version = JustEnoughCalculation.Reference.VERSION,
        dependencies = "required-after:jei@[4.2.7.241,)", guiFactory = "JECGuiFactory")
public class JustEnoughCalculation {
    @Mod.Instance(JustEnoughCalculation.Reference.MODID)
    public static JustEnoughCalculation instance;
    @SidedProxy(modId = "jecalculation", clientSide = "me.towdium.jecalculation.network.ProxyClient", serverSide = "me.towdium.jecalculation.network.ProxyServer")
    public static IProxy proxy;
    public static SimpleNetworkWrapper networkWrapper;
    public static Logger log = LogManager.getLogger(Reference.MODID);

    public static Item itemCalculator = new ItemCalculator();
    public static Item itemFluidContainer = new ItemFluidContainer();
    public static Item itemLabel = new ItemLabel();

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        JECConfig.preInit(event);
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
        networkWrapper.registerMessage(PacketRecordModify.class, PacketRecordModify.class, 0, Side.SERVER);
        networkWrapper.registerMessage(PacketRecordSync.class, PacketRecordSync.class, 1, Side.CLIENT);
        networkWrapper.registerMessage(PacketSyncCalculator.class, PacketSyncCalculator.class, 2, Side.SERVER);
        networkWrapper.registerMessage(PacketOredictModify.class, PacketOredictModify.class, 3, Side.SERVER);
        proxy.preInit();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new JECCommand());
    }

    public static class Reference {
        public static final String MODID = "jecalculation";
        public static final String MODNAME = "Just Enough Calculation";
        public static final String VERSION = "@VERSION@";
    }
}
