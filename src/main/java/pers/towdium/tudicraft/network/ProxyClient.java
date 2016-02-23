package pers.towdium.tudicraft.network;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import pers.towdium.tudicraft.Tudicraft;
import pers.towdium.tudicraft.event.GuiEventHandler;
import pers.towdium.tudicraft.event.PlayerEventHandler;
import pers.towdium.tudicraft.gui.GuiHandler;

/**
 * @author Towdium
 */
public class ProxyClient implements IProxy {
    PlayerHandlerClient playerHandler = new PlayerHandlerClient();

    @Override
    public IPlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    @Override
    public void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(Tudicraft.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
    }

}
