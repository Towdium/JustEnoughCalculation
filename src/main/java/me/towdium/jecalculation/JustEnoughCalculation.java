package me.towdium.jecalculation;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.network.packets.PCalculator;
import me.towdium.jecalculation.network.packets.PEdit;
import me.towdium.jecalculation.network.packets.PRecord;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

import static me.towdium.jecalculation.JecaConfig.PATH;
import static me.towdium.jecalculation.gui.JecaGui.keyOpenGuiCraft;
import static me.towdium.jecalculation.gui.JecaGui.keyOpenGuiMath;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;
import static net.minecraftforge.fml.config.ModConfig.Type.COMMON;

@Mod.EventBusSubscriber(bus = MOD)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod(JustEnoughCalculation.MODID)
public class JustEnoughCalculation {
    public static final String MODID = "jecalculation";
    public static final String MODNAME = "Just Enough Calculation";
    public static final String PROTOCOL = "1";
    public static SimpleChannel network;
    public static Logger logger = LogManager.getLogger(MODID);

    public JustEnoughCalculation() {
        //noinspection ResultOfMethodCallIgnored
        Utilities.config().mkdirs();
        ModLoadingContext.get().registerConfig(COMMON, JecaConfig.common, FMLPaths.CONFIGDIR.get().resolve(PATH).toString());
    }

    @SubscribeEvent
    public static void setupCommon(FMLCommonSetupEvent event) {
        network = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
        );
        network.registerMessage(0, PCalculator.class, PCalculator::write, PCalculator::new, PCalculator::handle);
        network.registerMessage(1, PEdit.class, PEdit::write, PEdit::new, PEdit::handle);
        network.registerMessage(2, PRecord.class, PRecord::write, PRecord::new, PRecord::handle);
        CapabilityManager.INSTANCE.register(JecaCapability.Container.class, new JecaCapability.Storage(), JecaCapability.Container::new);
        ILabel.initServer();
    }

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        ILabel.initClient();
        Controller.loadFromLocal();
        ClientRegistry.registerKeyBinding(keyOpenGuiCraft);
        ClientRegistry.registerKeyBinding(keyOpenGuiMath);
    }
}
