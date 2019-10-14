package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: Towdium
 * Date: 16/02/19
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WOverlay extends WContainer {
    @Override
    public boolean onPressed(JecaGui gui, int key, int modifier) {
        if (super.onPressed(gui, key, modifier)) return true;
        if (key == GLFW.GLFW_KEY_ESCAPE) {
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
