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
@Mod(modid = Tags.MODID,
     name = Tags.MODNAME,
     version = Tags.VERSION,
     dependencies = "required-after:NotEnoughItems")
public class JustEnoughCalculation {
    @Mod.Instance(Tags.MODID)
    public static JustEnoughCalculation INSTANCE;

    public static ClientHandler handler = new ClientHandler();

    public static Logger logger = LogManager.getLogger(Tags.MODID);

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

}
