package me.towdium.jecalculation.network;

import cpw.mods.fml.client.registry.ClientRegistry;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiCalculator;
import me.towdium.jecalculation.command.JecaCommand;
import me.towdium.jecalculation.data.label.ILabel;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.input.Keyboard;

public class ProxyClient extends ProxyServer {
    public static final KeyBinding keyOpenGui =
            new KeyBinding("key.open_gui", Keyboard.KEY_NONE, "key.category");

    @Override
    public void initPre() {
        super.initPre();
    }

    @Override
    public void initPost() {
        super.initPost();
        ClientCommandHandler.instance.registerCommand(new JecaCommand());
        ILabel.initClient();
    }

    @Override
    public void init() {
        super.init();
        ClientRegistry.registerKeyBinding(keyOpenGui);
    }


    @Override
    public void displayCalculator() {
        super.displayCalculator();
        JecaGui.displayGui(new GuiCalculator());
    }
}
