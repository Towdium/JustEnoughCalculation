package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
@OnlyIn(Dist.CLIENT)
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
        widgets.forEach(i -> {
            if (i.onDraw(gui, mouseX, mouseY)) w.value = i;
        });
        if (w.value != null) w.value.onDraw(gui, mouseX, mouseY);
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
    public boolean onKeyPressed(JecaGui gui, int key, int modifier) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onKeyPressed(gui, key, modifier));
    }

    @Override
    public boolean onKeyReleased(JecaGui gui, int key, int modifier) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onKeyReleased(gui, key, modifier));
    }

    @Override
    public boolean onMouseReleased(JecaGui gui, int xMouse, int yMouse, int button) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onMouseReleased(gui, xMouse, yMouse, button));
    }

    @Override
    public boolean onChar(JecaGui gui, char ch, int modifier) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onChar(gui, ch, modifier));
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onMouseScroll(gui, xMouse, yMouse, diff));
    }

    @Override
    public boolean onMouseDragged(JecaGui gui, int xMouse, int yMouse, int xDrag, int yDrag) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onMouseDragged(gui, xMouse, yMouse, xDrag, yDrag));
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

    @Override
    public void onTick(JecaGui gui) {
        widgets.forEach(i -> i.onTick(gui));
    }
}
