package me.towdium.jecalculation.client.gui.drawables;

import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
public abstract class DTooltip implements IDrawable {
    @Nullable
    public String name;
    Utilities.Timer timer = new Utilities.Timer();

    public DTooltip(@Nullable String name) {
        this.name = name;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        if (name != null) {
            timer.setState(mouseIn(xMouse, yMouse));
            if (timer.getTime() > 500) {
                Pair<String, Boolean> str = Utilities.L18n.search(String.join(".", "gui", name, "tooltip"));
                if (str.two || JecGui.ALWAYS_TOOLTIP) gui.drawTooltip(xMouse, yMouse, str.one);
            }
        }
    }

    abstract boolean mouseIn(int xMouse, int yMouse);
}
