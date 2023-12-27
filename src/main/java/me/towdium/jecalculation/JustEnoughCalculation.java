package me.towdium.jecalculation;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import me.towdium.jecalculation.network.CommonProxy;

/**
 * @author Towdium
 */
@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
@Mod(
    modid = Tags.MODID,
    name = Tags.MODNAME,
    version = Tags.VERSION,
    dependencies = "required-after:NotEnoughItems",
    acceptedMinecraftVersions = "[1.7.10]")
public class JustEnoughCalculation {

    public static Logger logger = LogManager.getLogger(Tags.MODID);

    @SidedProxy(
        clientSide = Tags.GROUPNAME + ".network.ClientProxy",
        serverSide = Tags.GROUPNAME + ".network.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(Tags.MODID)
    public static JustEnoughCalculation INSTANCE;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        proxy.serverAboutToStart(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        proxy.serverStarted(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        proxy.serverStopping(event);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        proxy.serverStopped(event);
    }
}
