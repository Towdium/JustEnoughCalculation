package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IWidget;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.Utilities.L18n;
import me.towdium.jecalculation.utils.wrappers.Pair;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class WTooltip implements IWidget {
    @Nullable
    public String name;
    Utilities.Timer timer = new Utilities.Timer();

    public WTooltip(@Nullable String name) {
        this.name = name;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        if (name != null) {
            timer.setState(mouseIn(xMouse, yMouse));
            if (timer.getTime() > 500) {
                Pair<String, Boolean> str = L18n.search(String.join(".", "gui", name, "tooltip"));
                if (str.two || JecGui.ALWAYS_TOOLTIP) gui.drawTooltip(xMouse, yMouse, str.one);
            }
        }
    }

    abstract boolean mouseIn(int xMouse, int yMouse);
}
