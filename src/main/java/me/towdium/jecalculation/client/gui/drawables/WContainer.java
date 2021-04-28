package me.towdium.jecalculation.client.gui.drawables;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.IWidget;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-14.
 */
@ParametersAreNonnullByDefault
public class WContainer implements IWidget {
    protected List<IWidget> widgets = new ArrayList<>();
    protected JecGui gui;

    public void add(IWidget w) {
        widgets.add(w);
    }

    public void addAll(IWidget... w) {
        for (IWidget aw : w)
            add(aw);
    }

    public void remove(IWidget w) {
        widgets.remove(w);
    }

    public void removeAll(IWidget... w) {
        for (IWidget aw : w)
            remove(aw);
    }

    public void clear() {
        widgets.clear();
    }

    @Override
    public void onDraw(JecGui gui, int mouseX, int mouseY) {
        widgets.forEach(widget -> widget.onDraw(gui, mouseX, mouseY));
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        for (IWidget w : widgets) if (w.onClicked(gui, xMouse, yMouse, button)) return true;
        return false;
    }

    @Override
    public boolean onKey(JecGui gui, char ch, int code) {
        for (IWidget w : widgets) if (w.onKey(gui, ch, code)) return true;
        return false;
    }

    @Override
    public boolean onScroll(JecGui gui, int xMouse, int yMouse, int diff) {
        for (IWidget w : widgets) if (w.onScroll(gui, xMouse, yMouse, diff)) return true;
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
