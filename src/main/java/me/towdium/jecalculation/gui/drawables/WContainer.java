package me.towdium.jecalculation.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.IWidget;
import me.towdium.jecalculation.utils.Utilities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-14.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WContainer implements IWidget {
    protected List<IWidget> widgets = new ArrayList<>();

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
    public void onDraw(JecaGui gui, int mouseX, int mouseY) {
        widgets.forEach(widget -> widget.onDraw(gui, mouseX, mouseY));
    }

    @Override
    public boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        Utilities.ReversedIterator<IWidget> i = new Utilities.ReversedIterator<>(widgets);
        while (i.hasNext()) if (i.next().onClicked(gui, xMouse, yMouse, button)) return true;
        return false;
    }

    @Override
    public boolean onKey(JecaGui gui, char ch, int code) {
        Utilities.ReversedIterator<IWidget> i = new Utilities.ReversedIterator<>(widgets);
        while (i.hasNext()) if (i.next().onKey(gui, ch, code)) return true;
        return false;
    }

    @Override
    public boolean onScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        Utilities.ReversedIterator<IWidget> i = new Utilities.ReversedIterator<>(widgets);
        while (i.hasNext()) if (i.next().onScroll(gui, xMouse, yMouse, diff)) return true;
        return false;
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        Utilities.ReversedIterator<IWidget> i = new Utilities.ReversedIterator<>(widgets);
        while (i.hasNext()) if (i.next().onTooltip(gui, xMouse, yMouse, tooltip)) return true;
        return false;
    }
}
