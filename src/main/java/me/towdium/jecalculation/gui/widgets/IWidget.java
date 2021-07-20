package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.wrappers.Wrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
public interface IWidget {
    default boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        return false;
    }

    default boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        return false;
    }

    default boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        return false;
    }

    default void onMouseFocused(JecaGui gui, int xMouse, int yMouse, int button) {
    }

    default boolean onMouseReleased(JecaGui gui, int xMouse, int yMouse, int button) {
        return false;
    }

    default boolean onKeyPressed(JecaGui gui, char ch, int code) {
        return false;
    }

    default boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        return false;
    }

    default boolean getLabelUnderMouse(int xMouse, int yMouse, Wrapper<ILabel> label) {
        return false;
    }

    @FunctionalInterface
    interface ListenerValue<W extends IWidget, V> {
        void invoke(W widget, V value);
    }

    @FunctionalInterface
    interface ListenerAction<W extends IWidget> {
        void invoke(W widget);
    }
}
