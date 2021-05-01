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

import static org.lwjgl.input.Keyboard.KEY_NONE;

public class ClientHandler {
    public static final KeyBinding keyOpenGuiCraft = new KeyBinding("jecalculation.key.gui_craft", KEY_NONE, "jecalculation.key.category");
    public static final KeyBinding keyOpenGuiMath = new KeyBinding("jecalculation.key.gui_math", KEY_NONE, "jecalculation.key.category");

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
