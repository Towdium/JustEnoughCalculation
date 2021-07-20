package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Wrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-14.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WContainer implements IContainer {
    protected List<IWidget> widgets = new ArrayList<>();

    public void add(IWidget... w) {
        widgets.addAll(Arrays.asList(w));
    }

    public void remove(IWidget... w) {
        widgets.removeAll(Arrays.asList(w));
    }

    public void clear() {
        widgets.clear();
    }

    public boolean contains(IWidget w) {
        return widgets.contains(w);
    }

    @Override
    public boolean onDraw(JecaGui gui, int mouseX, int mouseY) {
        Wrapper<IWidget> w = new Wrapper<>(null);
        widgets.forEach(widget -> {
            if (widget.onDraw(gui, mouseX, mouseY))
                w.value = widget;
        });
        if (w.value != null)
            w.value.onDraw(gui, mouseX, mouseY);
        return false;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                                                        .anyMatch(i -> i.onMouseClicked(gui, xMouse, yMouse, button));
    }

    @Override
    public void onMouseFocused(JecaGui gui, int xMouse, int yMouse, int button) {
        widgets.forEach(i -> i.onMouseFocused(gui, xMouse, yMouse, button));
    }

    @Override
    public boolean onKeyPressed(JecaGui gui, char ch, int code) {
        return new Utilities.ReversedIterator<>(widgets).stream().anyMatch(i -> i.onKeyPressed(gui, ch, code));
    }

    @Override
    public boolean onMouseReleased(JecaGui gui, int xMouse, int yMouse, int button) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                                                        .anyMatch(i -> i.onMouseReleased(gui, xMouse, yMouse, button));
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                                                        .anyMatch(i -> i.onMouseScroll(gui, xMouse, yMouse, diff));
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                                                        .anyMatch(i -> i.onTooltip(gui, xMouse, yMouse, tooltip));
    }

    @Override
    public boolean getLabelUnderMouse(int xMouse, int yMouse, Wrapper<ILabel> label) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                                                        .anyMatch(i -> i.getLabelUnderMouse(xMouse, yMouse, label));
    }
}
