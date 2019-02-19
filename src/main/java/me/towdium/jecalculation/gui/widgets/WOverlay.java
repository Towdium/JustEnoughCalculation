package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: Towdium
 * Date: 16/02/19
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WOverlay extends WContainer {
    @Override
    public boolean onKey(JecaGui gui, char ch, int code) {
        if (super.onKey(gui, ch, code)) return true;
        if (code == Keyboard.KEY_ESCAPE) {
            gui.root.remove(this);
            return true;
        } else return false;
    }

    @Override
    public boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!super.onClicked(gui, xMouse, yMouse, button)) gui.root.remove(this);
        return true;
    }

    @Override
    public boolean onScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        if (!super.onScroll(gui, xMouse, yMouse, diff)) gui.root.remove(this);
        return true;
    }
}
