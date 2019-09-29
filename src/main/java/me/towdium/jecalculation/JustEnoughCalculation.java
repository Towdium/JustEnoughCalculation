package me.towdium.jecalculation;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.network.packets.PCalculator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

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
    public static final KeyBinding keyOpenGuiCraft = new KeyBinding("jecalculation.key.gui_craft", GLFW.GLFW_KEY_UNKNOWN, "jecalculation.key.category");
    //public static final KeyBinding keyOpenGuiMath = new KeyBinding("jecalculation.key.gui_math", GLFW.GLFW_KEY_UNKNOWN, "jecalculation.key.category");

    @SubscribeEvent
    public static void setupCommon(FMLCommonSetupEvent event) {
        network = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(MODID, "main"),
                () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
        );
        network.registerMessage(0, PCalculator.class, PCalculator::write, PCalculator::new, PCalculator::handle);
        CapabilityManager.INSTANCE.register(JecaCapability.Container.class, new JecaCapability.Storage(), JecaCapability.Container::new);
        ILabel.initServer();
    }

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        ILabel.initClient();
        Controller.loadFromLocal();
        ClientRegistry.registerKeyBinding(keyOpenGuiCraft);
        //ClientRegistry.registerKeyBinding(keyOpenGuiMath);
    }
}
