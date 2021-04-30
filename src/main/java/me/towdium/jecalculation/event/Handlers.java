package me.towdium.jecalculation.event;

import me.towdium.jecalculation.data.ControllerClient;
import me.towdium.jecalculation.event.handlers.InputEventHandler;

import java.util.ArrayList;

public class Handlers {
    public static final ArrayList<Object> handlers;

    static {
        handlers = new ArrayList<>();

        handlers.add(new InputEventHandler());
    }
}
