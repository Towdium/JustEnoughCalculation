package me.towdium.jecalculation.network;

import cpw.mods.fml.client.registry.ClientRegistry;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.guis.GuiCalculator;
import me.towdium.jecalculation.command.JecaCommand;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.input.Keyboard;

public class ProxyClient extends ProxyCommon{
    public static final KeyBinding keyOpenGui =
            new KeyBinding("key.open_gui", Keyboard.KEY_F, "key.category");


    @Override
    public void initPost() {
        super.initPost();
        JustEnoughCalculation.logger.info("client proxy init post");
        ClientCommandHandler.instance.registerCommand(new JecaCommand());
        new JecGui(null, new GuiCalculator());
    }

    @Override
    public void init() {
        super.init();
        JustEnoughCalculation.logger.info("client proxy init");
        ClientRegistry.registerKeyBinding(keyOpenGui);
    }


    @Override
    public void displayCalculator() {
        JecGui.displayGui(new GuiCalculator());
    }
}
