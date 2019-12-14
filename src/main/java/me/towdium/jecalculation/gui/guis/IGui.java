package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.IContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: Towdium
 * Date: 18-9-25
 */
@OnlyIn(Dist.CLIENT)
public interface IGui extends IContainer {
    default void onVisible(JecaGui gui) {
    }
}
