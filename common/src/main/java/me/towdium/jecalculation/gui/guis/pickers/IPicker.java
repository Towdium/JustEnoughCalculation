package me.towdium.jecalculation.gui.guis.pickers;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.guis.Gui;
import me.towdium.jecalculation.gui.guis.IGui;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Environment(EnvType.CLIENT)
public interface IPicker extends IGui {
    IPicker setCallback(Consumer<ILabel> callback);

    class Impl extends Gui implements IPicker {
        protected Consumer<ILabel> callback;

        @Override
        public Impl setCallback(Consumer<ILabel> callback) {
            this.callback = callback;
            return this;
        }

        protected void notifyLsnr(ILabel l) {
            if (callback != null && l != ILabel.EMPTY) callback.accept(l);
        }
    }
}
