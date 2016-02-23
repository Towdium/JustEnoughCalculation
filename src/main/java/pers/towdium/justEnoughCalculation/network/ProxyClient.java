package pers.towdium.justEnoughCalculation.network;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.event.GuiEventHandler;
import pers.towdium.justEnoughCalculation.event.PlayerEventHandler;
import pers.towdium.justEnoughCalculation.gui.GuiHandler;

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
        NetworkRegistry.INSTANCE.registerGuiHandler(JustEnoughCalculation.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
    }

    public void setPlayerHandler(PlayerHandlerClient playerHandler) {
        this.playerHandler = playerHandler;
    }
}
