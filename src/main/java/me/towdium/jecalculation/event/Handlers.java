package me.towdium.jecalculation.event;

import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.FMLCommonHandler;
import java.util.ArrayList;
import me.towdium.jecalculation.event.handlers.EBusEventHandler;
import me.towdium.jecalculation.event.handlers.FMLBusEventHandler;
import me.towdium.jecalculation.event.handlers.NEIEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class Handlers {
    public static final ArrayList<Object> fmlHandlers;
    public static final ArrayList<Object> ebHandlers;

    static {
        fmlHandlers = new ArrayList<>();
        fmlHandlers.add(new FMLBusEventHandler());

        ebHandlers = new ArrayList<>();
        ebHandlers.add(new EBusEventHandler());
    }

    public static void register() {
        Handlers.fmlHandlers.forEach(FMLCommonHandler.instance().bus()::register);
        Handlers.ebHandlers.forEach(MinecraftForge.EVENT_BUS::register);
        GuiContainerManager.addInputHandler(new NEIEventHandler());
    }
}
