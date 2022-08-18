package me.towdium.jecalculation;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.events.GuiScreenEventHandler;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.network.packets.PCalculator;
import me.towdium.jecalculation.network.packets.PEdit;
import me.towdium.jecalculation.network.packets.PRecord;
import me.towdium.jecalculation.utils.Utilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

import static me.towdium.jecalculation.gui.JecaGui.keyOpenGuiCraft;
import static me.towdium.jecalculation.gui.JecaGui.keyOpenGuiMath;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JustEnoughCalculation {
    public static final String MODID = "jecalculation";
    public static final String MODNAME = "Just Enough Calculation";
    public static final String PROTOCOL = "1";
    public static NetworkChannel network;
    public static Logger logger = LogManager.getLogger(MODID);
    @Environment(EnvType.CLIENT)
    public static GuiScreenEventHandler GUI_HANDLER;

    static {
        if (Platform.getEnv() == EnvType.CLIENT)
            GUI_HANDLER = new GuiScreenEventHandler();
    }

    public JustEnoughCalculation() {
        JecaItem.register();
        registerEvents();
        if (Platform.getEnv() == EnvType.CLIENT)
            registerClientEvents();

        //noinspection ResultOfMethodCallIgnored
        Utilities.config().mkdirs();
    }

    private static void registerEvents() {
        LifecycleEvent.SETUP.register(JustEnoughCalculation::setupCommon);
        PlayerEvent.PLAYER_JOIN.register(Controller.Server::onJoin);
    }

    @Environment(EnvType.CLIENT)
    private static void registerClientEvents() {
        ClientLifecycleEvent.CLIENT_SETUP.register(JustEnoughCalculation::setupClient);
        JecaGui.registerEvents();
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(LPlaceholder::onLogOut);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(Controller.Client::onLogOut);
    }

    public static void setupCommon() {
        Utilities.Greetings.send(logger, MODID);
        network = NetworkChannel.create(new ResourceLocation(MODID, "main"));

        network.register(PCalculator.class, PCalculator::write, PCalculator::new, PCalculator::handle);
        network.register(PEdit.class, PEdit::write, PEdit::new, PEdit::handle);
        network.register(PRecord.class, PRecord::write, PRecord::new, PRecord::handle);
        ILabel.initServer();
    }

    public static void setupClient(Minecraft minecraft) {
        ILabel.initClient();
        Controller.loadFromLocal();
        KeyMappingRegistry.register(keyOpenGuiCraft);
        KeyMappingRegistry.register(keyOpenGuiMath);
    }
}
