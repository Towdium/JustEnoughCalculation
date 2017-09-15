package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-14.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DContainer implements IDrawable {
    protected List<IDrawable> widgets = new ArrayList<>();
    protected JecGui gui;

    public void add(IDrawable w) {
        widgets.add(w);
    }

    public void addAll(IDrawable... w) {
        for (IDrawable aw : w)
            add(aw);
    }

    public void remove(IDrawable w) {
        widgets.remove(w);
    }

    public void removeAll(IDrawable... w) {
        for (IDrawable aw : w)
            remove(aw);
    }

    public void onDraw(JecGui gui, int mouseX, int mouseY) {
        widgets.forEach(widget -> widget.onDraw(gui, mouseX, mouseY));
    }

    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        for (IDrawable w : widgets) {
            if (w.onClicked(gui, xMouse, yMouse, button)) return true;
        }
        return false;
    }

    public boolean onKey(JecGui gui, char ch, int code) {
        for (IDrawable w : widgets) {
            if (w.onKey(gui, ch, code)) return true;
        }
        return false;
    }

    /*public Optional<DLabel> getEntryAt(int xMouse, int yMouse) {
        return drawables.stream().map(w -> {
            if (w instanceof DLabelGroup) return ((DLabelGroup) w).getEntryAt(xMouse, yMouse);
            else if (w instanceof DLabel) return Optional.ofNullable(
                    ((DLabel) w).mouseIn(xMouse, yMouse) ? ((DLabel) w) : null);
            else return Optional.<DLabel>empty();
        }).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }*/
}
