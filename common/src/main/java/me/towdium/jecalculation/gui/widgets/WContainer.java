package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;

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
@Environment(EnvType.CLIENT)
public class WContainer implements IContainer {

    protected int offsetX;
    protected int offsetY;

    public WContainer() {
        this(0, 0);
    }

    public WContainer(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    protected List<IWidget> widgets = new ArrayList<>();

    public void add(IWidget... w) {
        widgets.addAll(Arrays.asList(w));
    }

    public void addAll(List<? extends IWidget> w) {
        widgets.addAll(w);
    }

    public void remove(IWidget... w) {
        widgets.removeAll(Arrays.asList(w));
    }

    public void removeAll(List<? extends IWidget> w) {
        widgets.removeAll(w);
    }

    public void clear() {
        widgets.clear();
    }

    public boolean contains(IWidget w) {
        return widgets.contains(w);
    }

    public List<IWidget> getWidgets() {
        return widgets;
    }

    @Override
    public boolean onDraw(JecaGui gui, int mouseX, int mouseY) {
        gui.getMatrix().pose().pushPose();
        gui.getMatrix().pose().translate(offsetX, offsetY, 0);
        gui.getItemOffsetStack().push(offsetX, offsetY);
        Wrapper<IWidget> w = new Wrapper<>(null);
        widgets.forEach(i -> {
            if (i.onDraw(gui, mouseX - offsetX, mouseY - offsetY)) w.value = i;
        });
        if (w.value != null) w.value.onDraw(gui, mouseX - offsetX, mouseY - offsetY);
        gui.getItemOffsetStack().pop();
        gui.getMatrix().pose().popPose();
        return false;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onMouseClicked(gui, xMouse - offsetX, yMouse - offsetY, button));
    }

    @Override
    public void onMouseFocused(JecaGui gui, int xMouse, int yMouse, int button) {
        widgets.forEach(i -> i.onMouseFocused(gui, xMouse - offsetX, yMouse - offsetY, button));
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
                .anyMatch(i -> i.onMouseReleased(gui, xMouse - offsetX, yMouse - offsetY, button));
    }

    @Override
    public boolean onChar(JecaGui gui, char ch, int modifier) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onChar(gui, ch, modifier));
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onMouseScroll(gui, xMouse - offsetX, yMouse - offsetY, diff));
    }

    @Override
    public boolean onMouseDragged(JecaGui gui, int xMouse, int yMouse, int xDrag, int yDrag) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onMouseDragged(gui, xMouse - offsetX, yMouse - offsetY, xDrag, yDrag));
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onTooltip(gui, xMouse - offsetX, yMouse - offsetY, tooltip));
    }

    @Override
    public boolean getLabelUnderMouse(int xMouse, int yMouse, Wrapper<ILabel> label) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.getLabelUnderMouse(xMouse - offsetX, yMouse - offsetY, label));
    }

    @Override
    public void onTick(JecaGui gui) {
        widgets.forEach(i -> i.onTick(gui));
    }
}
