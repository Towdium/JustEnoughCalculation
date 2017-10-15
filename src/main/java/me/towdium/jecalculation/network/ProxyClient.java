package me.towdium.jecalculation.network;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.guis.GuiCalculator;
import me.towdium.jecalculation.command.JecCommand;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@SideOnly(Side.CLIENT)
public class ProxyClient implements IProxy {
    public static final KeyBinding keyOpenGui = new KeyBinding("key.open_gui", KEY_NONE, "key.category");

    @Override
    public void initPost() {
        ClientCommandHandler.instance.registerCommand(new JecCommand());
        new JecGui(null, new GuiCalculator());
    }

    @Override
    public void init() {
        ClientRegistry.registerKeyBinding(keyOpenGui);
    }

    @Override
    public void displayCalculator() {
        JecGui.displayGui(new GuiCalculator());
    }
}
