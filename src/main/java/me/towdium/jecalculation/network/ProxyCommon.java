package me.towdium.jecalculation.network;

import me.towdium.jecalculation.event.Handlers;
import net.minecraftforge.common.MinecraftForge;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
public class ProxyCommon {
    public void initPre() {
        Handlers.handlers.forEach(MinecraftForge.EVENT_BUS::register);
    }

    public void init() {
    }

    public void initPost() {
    }
}
