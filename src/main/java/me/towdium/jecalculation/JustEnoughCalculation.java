package me.towdium.jecalculation;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.network.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

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

    public static ClientHandler handler = new ClientHandler();

    public static Logger logger = LogManager.getLogger(Reference.MODID);
    public static enumSide side = enumSide.CLIENT;

    @Mod.EventHandler
    public static void initPre(FMLPreInitializationEvent event) {
        JecaConfig.preInit(event);
        handler.initPre();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        handler.init();
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        handler.initPost();
        NEIPlugin.init();
    }

    public static class Reference {
        public static final String MODID = "jecalculation";
        public static final String MODNAME = "Just Enough Calculation";
        public static final String VERSION = "@VERSION@";
    }

    // Only to use at client side
    public enum enumSide {
        CLIENT,  // Running at client side and server not installed
        BOTH,  // Running at client side and both installed
        UNDEFINED  // Default unknown
    }
}
