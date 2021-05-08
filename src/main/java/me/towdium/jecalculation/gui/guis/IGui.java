package me.towdium.jecalculation.gui.guis;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.IContainer;

/**
 * Author: Towdium
 * Date: 18-9-25
 */
@SideOnly(Side.CLIENT)
public interface IGui extends IContainer {
    default void onVisible(JecaGui gui) {
    }

    default boolean acceptsTransfer() {
        return false;
    }
}
