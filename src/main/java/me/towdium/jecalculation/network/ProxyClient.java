package me.towdium.jecalculation.network;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.guis.GuiCalculator;
import me.towdium.jecalculation.command.JecaCommand;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.event.Handlers;
import me.towdium.jecalculation.item.ItemCalculator;
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
        JecGui.displayGui(new GuiCalculator());
    }
}
