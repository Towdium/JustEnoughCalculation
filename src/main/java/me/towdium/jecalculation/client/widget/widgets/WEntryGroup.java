package me.towdium.jecalculation.client.widget.widgets;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.widget.Widget;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
public class WEntryGroup extends Widget.Advanced {
    ArrayList<WEntry> widgets = new ArrayList<>();

    public WEntryGroup(int xPos, int yPos, int column, int row) {
        this(xPos, yPos, column, row, 18, 18);
    }

    public WEntryGroup(int xPos, int yPos, int column, int row, int xSize, int ySize) {
        IntStream.range(0, column).forEach(c -> IntStream.range(0, row).forEach(r ->
                widgets.add(new WEntry(xPos + c * xSize, yPos + r * ySize, xSize, ySize))));
    }

    @Override
    public void onGuiInit(JecGui gui) {
        widgets.forEach(w -> w.onGuiInit(gui));
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        widgets.forEach(w -> w.onDraw(gui, xMouse, yMouse));
    }

    @Override
    public void onRemoved(JecGui gui) {
        widgets.forEach(w -> w.onRemoved(gui));
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        return widgets.stream().anyMatch(w -> w.onClicked(gui, xMouse, yMouse, button));
    }

    public Optional<WEntry> getEntryAt(JecGui gui, int xMouse, int yMouse) {
        return widgets.stream().filter(w -> w.mouseIn(gui, xMouse, yMouse)).findFirst();
    }
}
