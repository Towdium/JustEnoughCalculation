package me.towdium.jecalculation.network;

import me.towdium.jecalculation.command.JecCommand;
import net.minecraftforge.client.ClientCommandHandler;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
public class ProxyClient extends ProxyCommon {
    @Override
    public void initPost() {
        super.initPost();
        ClientCommandHandler.instance.registerCommand(new JecCommand());
    }
}
