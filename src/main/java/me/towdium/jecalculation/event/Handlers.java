package me.towdium.jecalculation.event;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.event.handlers.RegisterEventHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Handlers {
    public static final ArrayList<Object> handlers;

    static {
        handlers = new ArrayList<>();

        handlers.add(new RegisterEventHandler());
    }
}
