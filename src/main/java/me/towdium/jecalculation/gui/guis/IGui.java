package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.IContainer;

/**
 * Author: Towdium
 * Date: 18-9-25
 */
public interface IGui extends IContainer {
    default void onVisible(JecaGui gui) {
    }
}
