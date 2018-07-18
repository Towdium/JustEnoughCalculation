package me.towdium.jecalculation.gui;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.drawables.WContainer;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public interface IWPicker extends IWidget {
    IWPicker setCallback(Consumer<ILabel> callback);

    class Simple extends WContainer implements IWPicker {
        protected Wrapper<Consumer<ILabel>> callback = new Wrapper<>(null);

        @Override
        public IWPicker setCallback(Consumer<ILabel> callback) {
            this.callback.value = callback;
            return this;
        }
    }
}
