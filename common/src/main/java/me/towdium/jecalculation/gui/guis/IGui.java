package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.IContainer;
import me.towdium.jecalculation.gui.widgets.IWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Author: Towdium
 * Date: 18-9-25
 */
@Environment(EnvType.CLIENT)
public interface IGui extends IContainer {
    default void onVisible(JecaGui gui) {
    }

    default boolean acceptsTransfer() {
        return false;
    }

    default boolean acceptsLabel() {
        return false;
    }

    void setOverlay(IWidget w);
}
