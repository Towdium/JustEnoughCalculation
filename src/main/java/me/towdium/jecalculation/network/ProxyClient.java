package me.towdium.jecalculation.network;

import me.towdium.jecalculation.command.JecCommand;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
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
public class ProxyClient extends ProxyServer {
    public static final KeyBinding keyOpenGui = new KeyBinding("key.open_gui", KEY_NONE, "key.category");

    @Override
    public void initPost() {
        super.initPost();
        ClientCommandHandler.instance.registerCommand(new JecCommand());
        ILabel.initClient();
        Controller.loadFromLocal();
    }

    @Override
    public void init() {
        super.init();
        ClientRegistry.registerKeyBinding(keyOpenGui);
    }

    @Override
    public void runOnSide(Runnable r, Side s) {
        if (s == Side.CLIENT) r.run();
    }

    @Override
    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }
}
