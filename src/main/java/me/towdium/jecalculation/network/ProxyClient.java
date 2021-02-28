package me.towdium.jecalculation.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.event.GuiEventHandler;
import me.towdium.jecalculation.event.PlayerEventHandler;
import me.towdium.jecalculation.gui.GuiHandler;
import net.minecraftforge.common.MinecraftForge;

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
