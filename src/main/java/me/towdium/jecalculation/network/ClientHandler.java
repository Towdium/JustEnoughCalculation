package me.towdium.jecalculation.network;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.command.JecaCommand;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.event.Handlers;
import net.minecraftforge.client.ClientCommandHandler;

import static me.towdium.jecalculation.gui.JecaGui.keyOpenGuiCraft;
import static me.towdium.jecalculation.gui.JecaGui.keyOpenGuiMath;

@SideOnly(Side.CLIENT)
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
}
