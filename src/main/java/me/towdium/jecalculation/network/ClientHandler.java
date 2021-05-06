package me.towdium.jecalculation.network;

import cpw.mods.fml.client.registry.ClientRegistry;
import me.towdium.jecalculation.command.JecaCommand;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.event.Handlers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.input.Keyboard;

import static me.towdium.jecalculation.gui.JecaGui.keyOpenGuiCraft;
import static me.towdium.jecalculation.gui.JecaGui.keyOpenGuiMath;
import static org.lwjgl.input.Keyboard.KEY_NONE;

public class ClientHandler {

    public void initPre() {
        Handlers.register();
    }

    public void initPost() {
        ILabel.initServer();
        ClientCommandHandler.instance.registerCommand(new JecaCommand());
        ILabel.initClient();
        Controller.loadFromLocal();
    }

    public void init() {
        ClientRegistry.registerKeyBinding(keyOpenGuiCraft);
        ClientRegistry.registerKeyBinding(keyOpenGuiMath);
    }


    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }
}
