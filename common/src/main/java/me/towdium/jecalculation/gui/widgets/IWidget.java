package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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

    /**
     * This function is basically an not cancellable mouse click event before real mouse click event,
     * when widgets check if it should be focused, if mouse not in, then give up focus
     */
    default void onMouseFocused(JecaGui gui, int xMouse, int yMouse, int button) {
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

    default boolean getLabelUnderMouse(int xMouse, int yMouse, Wrapper<ILabel> label) {
        return false;
    }

//    default void onTick(JecaGui gui) {
//    }

    @FunctionalInterface
    interface ListenerValue<W extends IWidget, V> {
        void invoke(W widget, V value);
    }

    @FunctionalInterface
    interface ListenerAction<W extends IWidget> {
        void invoke(W widget);
    }
}
