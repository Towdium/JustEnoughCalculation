package me.towdium.jecalculation;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import me.towdium.jecalculation.gui.GuiHandler;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.network.IProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Towdium
 */

@SuppressWarnings("unused")
@Mod(modid = JustEnoughCalculation.Reference.MODID,
     name = JustEnoughCalculation.Reference.MODNAME,
     version = JustEnoughCalculation.Reference.VERSION,
     dependencies = "required-after:NotEnoughItems")
public class JustEnoughCalculation {
    @Mod.Instance(Reference.MODID)
    public static JustEnoughCalculation INSTANCE;

    @SidedProxy(modId = Reference.MODID,
                clientSide = "me.towdium.jecalculation.network.ProxyClient",
                serverSide = "me.towdium.jecalculation.network.ProxyServer")
    public static IProxy proxy;

    public static SimpleNetworkWrapper network;
    public static Logger logger = LogManager.getLogger(Reference.MODID);

    @Mod.EventHandler
    public static void initPre(FMLPreInitializationEvent event) {
        JecaConfig.preInit(event);
        GameRegistry.registerItem(JecaItem.INSTANCE, JecaItem.NAME);
        network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());
        proxy.init();
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        NEIPlugin.init();
    }


    public static class Reference {
        public static final String MODID = "jecalculation";
        public static final String MODNAME = "Just Enough Calculation";
        public static final String VERSION = "@VERSION@";
    }
}
