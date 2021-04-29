package me.towdium.jecalculation.event;

import me.towdium.jecalculation.event.handlers.ControllerClient;
import me.towdium.jecalculation.event.handlers.ControllerServer;
import me.towdium.jecalculation.event.handlers.InputEventHandler;

import java.util.ArrayList;

public class Handlers {
    public static final ArrayList<Object> handlers;

    static {
        handlers = new ArrayList<>();

        handlers.add(new InputEventHandler());
        handlers.add(new ControllerClient());
        handlers.add(new ControllerServer());
    }
}
