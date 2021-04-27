package me.towdium.jecalculation;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.command.JecaCommand;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.network.ProxyCommon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

/**
 * @author Towdium
 */

@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
@Mod(modid = JustEnoughCalculation.Reference.MODID,
     name = JustEnoughCalculation.Reference.MODNAME,
     version = JustEnoughCalculation.Reference.VERSION,
     dependencies = "required-after:NotEnoughItems")
public class JustEnoughCalculation {
    @Mod.Instance(Reference.MODID)
    public static JustEnoughCalculation INSTANCE;

    @SidedProxy(modId = Reference.MODID,
                clientSide = "me.towdium.jecalculation.network.ProxyClient",
                serverSide = "me.towdium.jecalculation.network.ProxyCommon")
    public static ProxyCommon proxy;

    public static SimpleNetworkWrapper network;
    public static Logger logger = LogManager.getLogger(Reference.MODID);
    public static enumSide side = enumSide.UNDEFINED;

    @NetworkCheckHandler
    public static boolean networkCheck(Map<String, String> mods, Side s) {
        if (s == Side.SERVER) {
            if (mods.containsKey(Reference.MODID) && !JecaConfig.isForceClient()) {
                side = enumSide.BOTH;
            } else {
                side = enumSide.CLIENT;
            }
        } else {
            if (mods.containsKey(Reference.MODID)) {
                side = enumSide.SERVER;
            } else {
                return false;
            }
        }
        return true;
    }

    @Mod.EventHandler
    public static void initPre(FMLPreInitializationEvent event) {
        JecaConfig.preInit(event);
        network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
        proxy.initPre();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        proxy.initPost();
        NEIPlugin.init();
    }

    @Mod.EventHandler
    public static void onServerStart(FMLServerStartingEvent event) {
        if (side == enumSide.SERVER) {
            event.registerServerCommand(new JecaCommand());
        }
    }


    public static class Reference {
        public static final String MODID = "jecalculation";
        public static final String MODNAME = "Just Enough Calculation";
        public static final String VERSION = "@VERSION@";
    }

    public enum enumSide {
        /**
         * Running at client side and server not installed.
         */
        CLIENT,
        /**
         * Running at server side whether client is installed.
         */
        SERVER,
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
