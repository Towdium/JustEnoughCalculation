package me.towdium.jecalculation.event;

import cpw.mods.fml.common.FMLCommonHandler;
import me.towdium.jecalculation.event.handlers.EBEventHandler;
import me.towdium.jecalculation.event.handlers.FMLBusEventHandler;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

public class Handlers {
    public static final ArrayList<Object> fmlHandlers;
    public static final ArrayList<Object> ebHandlers;

    static {
        fmlHandlers = new ArrayList<>();
        fmlHandlers.add(new FMLBusEventHandler());

        ebHandlers = new ArrayList<>();
        ebHandlers.add(new EBEventHandler());
    }

    public static void register() {
        Handlers.fmlHandlers.forEach(FMLCommonHandler.instance().bus()::register);
        Handlers.ebHandlers.forEach(MinecraftForge.EVENT_BUS::register);
    }
}
