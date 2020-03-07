package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IWidget {
    boolean onDraw(JecaGui gui, int xMouse, int yMouse);

    default boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        return false;
    }

    default boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        return false;
    }

    default boolean onMouseDragged(JecaGui gui, int xMouse, int yMouse, int xDrag, int yDrag) {
        return false;
    }

    default boolean onMouseReleased(JecaGui gui, int xMouse, int yMouse, int button) {
        return false;
    }

    default boolean onKeyPressed(JecaGui gui, int key, int modifier) {
        return false;
    }

    default boolean onKeyReleased(JecaGui gui, int key, int modifier) {
        return false;
    }

    default boolean onChar(JecaGui gui, char ch, int modifier) {
        return false;
    }

    default boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        return false;
    }

    @Nullable
    default WLabel getLabelUnderMouse(int xMouse, int yMouse) {
        return null;
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
