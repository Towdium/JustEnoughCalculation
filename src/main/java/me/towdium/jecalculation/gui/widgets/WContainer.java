package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.Utilities;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Author: towdium
 * Date:   17-9-14.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WContainer implements IContainer {
    protected List<IWidget> widgets = new ArrayList<>();
    protected WOverlay overlay = null;

    public void add(IWidget w) {
        widgets.add(w);
    }

    public void add(IWidget... w) {
        if (w.length == 1)
            widgets.add(w[0]);
        else
            widgets.addAll(Arrays.asList(w));
    }

    public void remove(IWidget... w) {
        if (w.length == 1)
            widgets.remove(w[0]);
        else
            widgets.removeAll(Arrays.asList(w));
    }

    public void setOverlay(@Nullable WOverlay overlay) {
        this.overlay = overlay;
    }

    public void clear() {
        widgets.clear();
    }

    public boolean contains(IWidget w) {
        return widgets.contains(w);
    }

    @Override
    public boolean onDraw(JecaGui gui, int mouseX, int mouseY) {
        IWidget[] w = new IWidget[1];
        widgets.forEach(widget -> {
            if (widget.onDraw(gui, mouseX, mouseY))
                w[0] = widget;
        });
        if (w[0] != null)
            w[0].onDraw(gui, mouseX, mouseY);
        if (overlay != null)
            overlay.onDraw(gui, mouseX, mouseY);
        return false;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        boolean b = overlay != null && overlay.onMouseClicked(gui, xMouse, yMouse, button);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                                                             .anyMatch(i -> i.onMouseClicked(gui, xMouse, yMouse,
                                                                                             button));
    }

    @Override
    public boolean onKeyPressed(JecaGui gui, char ch, int code) {
        boolean b = overlay != null && overlay.onKeyPressed(gui, ch, code);
        return b || new Utilities.ReversedIterator<>(widgets).stream().anyMatch(i -> i.onKeyPressed(gui, ch, code));
    }

    @Override
    public boolean onMouseReleased(JecaGui gui, int xMouse, int yMouse, int button) {
        boolean b = overlay != null && overlay.onMouseReleased(gui, xMouse, yMouse, button);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                                                             .anyMatch(i -> i.onMouseReleased(gui, xMouse, yMouse,
                                                                                              button));
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        boolean b = overlay != null && overlay.onMouseScroll(gui, xMouse, yMouse, diff);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                                                             .anyMatch(i -> i.onMouseScroll(gui, xMouse, yMouse, diff));
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        boolean b = overlay != null && overlay.onTooltip(gui, xMouse, yMouse, tooltip);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                                                             .anyMatch(i -> i.onTooltip(gui, xMouse, yMouse, tooltip));
    }

    @Nullable
    @Override
    public WLabel getLabelUnderMouse(int xMouse, int yMouse) {
        if(overlay != null && overlay.mouseIn(xMouse, yMouse)) {
            return overlay.getLabelUnderMouse(xMouse, yMouse);
        } else {
            return new Utilities.ReversedIterator<>(widgets).stream()
                                                            .map(i -> i.getLabelUnderMouse(xMouse, yMouse))
                                                            .filter(Objects::nonNull)
                                                            .findFirst()
                                                            .orElse(null);
        }
    }
}
