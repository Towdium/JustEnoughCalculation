package me.towdium.jecalculation.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Author: towdium
 * Date:   17-10-10.
 */
public interface IProxy {
    default void initPre() {
    }

    default void init() {
    }

    default void initPost() {
    }

    default void runOnSide(Runnable r, Side s) {
    }

    default EntityPlayer getPlayer() {
        return null;
    }
}
