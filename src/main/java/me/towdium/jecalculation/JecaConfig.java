package me.towdium.jecalculation;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@Config(modid = JustEnoughCalculation.Reference.MODID, name = "JustEnoughCalculation/main")
public class JecaConfig {
    @Config.Comment("Set to true to force client mode: no item and recipe allowed, use key bindings instead.")
    @Config.LangKey("config.force_client")
    public static boolean clientMode = false;

    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber
    public static class ConfigHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(JustEnoughCalculation.Reference.MODID)) {
                ConfigManager.sync(JustEnoughCalculation.Reference.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
