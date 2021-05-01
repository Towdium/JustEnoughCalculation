package me.towdium.jecalculation.gui.guis.pickers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.gui.widgets.IWidget;
import me.towdium.jecalculation.gui.widgets.WContainer;
import me.towdium.jecalculation.data.label.ILabel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public interface IPicker extends IGui {
    IPicker setCallback(Consumer<ILabel> callback);

    class Impl extends WContainer implements IPicker {
        protected Consumer<ILabel> callback;

        @Override
        public Impl setCallback(Consumer<ILabel> callback) {
            this.callback = callback;
            return this;
        }
    }
}
