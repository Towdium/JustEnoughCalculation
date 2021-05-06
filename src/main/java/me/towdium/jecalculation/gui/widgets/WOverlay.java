package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.gui.JecaGui;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: Towdium
 * Date: 16/02/19
 */
@ParametersAreNonnullByDefault
public class WOverlay extends WContainer {
    @Override
    public boolean onKeyPressed(JecaGui gui, char ch, int code) {
        if (super.onKeyPressed(gui, ch, code)) return true;
        if (code == Keyboard.KEY_ESCAPE) {
            gui.root.remove(this);
            return true;
        } else return false;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!super.onMouseClicked(gui, xMouse, yMouse, button)) gui.root.remove(this);
        return true;
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        if (!super.onMouseScroll(gui, xMouse, yMouse, diff)) gui.root.remove(this);
        return true;
    }
}
