package me.towdium.jecalculation.network;

import me.towdium.jecalculation.command.JecCommand;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
public class ProxyClient extends ProxyCommon {
    public static final KeyBinding keyOpenGui =
            new KeyBinding("key.open_gui", org.lwjgl.input.Keyboard.KEY_F, "key.category");

    @Override
    public void initPost() {
        super.initPost();
        ClientCommandHandler.instance.registerCommand(new JecCommand());
    }

    @Override
    public void init() {
        super.init();
        ClientRegistry.registerKeyBinding(keyOpenGui);
    }
}
