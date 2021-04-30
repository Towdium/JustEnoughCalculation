package me.towdium.jecalculation.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.drawables.WContainer;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.wrappers.Wrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public interface IWPicker extends IWidget {
    IWPicker setCallback(Consumer<ILabel> callback);

    class Impl extends WContainer implements IWPicker {
        protected Consumer<ILabel> callback;

        @Override
        public IWPicker setCallback(Consumer<ILabel> callback) {
            this.callback = callback;
            return this;
        }
    }
}
