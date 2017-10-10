package me.towdium.jecalculation;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.command.JecCommand;
import me.towdium.jecalculation.network.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

/**
 * @author Towdium
 */
@Mod.EventBusSubscriber
@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod(
        modid = JustEnoughCalculation.Reference.MODID,
        name = JustEnoughCalculation.Reference.MODNAME,
        version = JustEnoughCalculation.Reference.VERSION,
        dependencies = "required-after:jei@[4.7.6.89,)"
        //clientSideOnly = true
)
public class JustEnoughCalculation {
    @Mod.Instance(JustEnoughCalculation.Reference.MODID)
    public static JustEnoughCalculation instance;
    @SidedProxy(modId = "jecalculation",
            clientSide = "me.towdium.jecalculation.network.ProxyClient",
            serverSide = "me.towdium.jecalculation.network.ProxyServer")
    public static IProxy proxy;
    public static SimpleNetworkWrapper network;
    public static Logger logger = LogManager.getLogger(Reference.MODID);
    public static enumSide side = enumSide.UNDEFINED;

    @NetworkCheckHandler
    public static boolean networkCheck(Map<String, String> mods, Side s) {
        if (s == Side.SERVER) {
            if (mods.containsKey(Reference.MODID) && !JecConfig.forceClient) side = enumSide.BOTH;
            else side = enumSide.CLIENT;
            return true;
        } else return mods.containsKey(Reference.MODID);
    }

    @Mod.EventHandler
    public static void initPre(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
        proxy.initPre();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public static void initPost(FMLPostInitializationEvent event) {
        proxy.initPost();
    }

    @Mod.EventHandler
    public static void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new JecCommand());
    }

    public static class Reference {
        public static final String MODID = "jecalculation";
        public static final String MODNAME = "Just Enough Calculation";
        public static final String VERSION = "@VERSION@";
    }

    @SubscribeEvent
    public static void onJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        JustEnoughCalculation.logger.info("Join world");
    }

    public enum enumSide {
        /**
         * Running at client side and server not installed.
         */
        CLIENT,
        /**
         * Running at client side and both installed.
         */
        BOTH,
        /**
         * Default unknown.
         */
        UNDEFINED
    }
}
