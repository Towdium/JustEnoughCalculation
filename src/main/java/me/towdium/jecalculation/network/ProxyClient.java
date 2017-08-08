package me.towdium.jecalculation.network;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.event.*;
import me.towdium.jecalculation.gui.JECGuiHandler;
import me.towdium.jecalculation.model.ColorLabel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Towdium
 */

@SideOnly(Side.CLIENT)
public class ProxyClient implements IProxy {
    static PlayerHandlerSP playerHandler = new PlayerHandlerSP();

    @Override
    public void init() {

        NetworkRegistry.INSTANCE.registerGuiHandler(JustEnoughCalculation.instance, new JECGuiHandler());
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new InputEventHandler());
        MinecraftForge.EVENT_BUS.register(new ModelEventHandler());
        MinecraftForge.EVENT_BUS.register(new DataEventHandler());
        MinecraftForge.EVENT_BUS.register(new TooltipEventHandler());
        MinecraftForge.EVENT_BUS.register(new RegisterEventHandler());
    }

    @Override
    public void postInit() {
        Minecraft.getMinecraft().getItemColors()
                .registerItemColorHandler(new ColorLabel(), JustEnoughCalculation.itemLabel);
    }

    @Override
    public IPlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
