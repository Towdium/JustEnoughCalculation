package me.towdium.jecalculation.network;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import me.towdium.jecalculation.data.ControllerClient;
import me.towdium.jecalculation.event.Handlers;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiCalculator;
import me.towdium.jecalculation.command.JecaCommand;
import me.towdium.jecalculation.data.label.ILabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public class ClientHandler {
    public static final KeyBinding keyOpenGui =
            new KeyBinding("key.open_gui", Keyboard.KEY_NONE, "key.category");

    public void initPre() {
        Handlers.handlers.forEach(FMLCommonHandler.instance().bus()::register);
    }

    public void initPost() {
        ILabel.initServer();
        ClientCommandHandler.instance.registerCommand(new JecaCommand());
        ILabel.initClient();
        ControllerClient.loadFromLocal();
    }

    public void init() {
        ClientRegistry.registerKeyBinding(keyOpenGui);
    }


    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }
}
