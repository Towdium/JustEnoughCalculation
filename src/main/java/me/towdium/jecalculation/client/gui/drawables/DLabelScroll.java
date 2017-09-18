package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.core.labels.ILabel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DLabelScroll extends DContainer {
    List<ILabel> labels = new ArrayList<>();
    DLabelGroup labelGroup;
    DScroll scroll;
    int xPos, yPos, column, row, current;

    public DLabelScroll(int xPos, int yPos, int column, int row, DLabel.enumMode mode) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.column = column;
        this.row = row;
        labelGroup = new DLabelGroup(xPos, yPos, column, row, mode);
        scroll = new DScroll(xPos + column * 18 + 4, yPos, row * 18).setLsnrScroll(this::update);
        add(labelGroup);
        add(scroll);
    }

    public void update(float f) {
        int step = getStepAmount();
        current = (int) (step * f);
        if (current == step) current--;
        labelGroup.setLabel(labels, current * column);
    }

    @Override
    public boolean onScroll(JecGui gui, int xMouse, int yMouse, int diff) {
        boolean in = JecGui.mouseIn(xPos, yPos, column * 18, row * 18, xMouse, yMouse);
        if (in) scroll.setCurrent(getPos(current - diff));
        return in;
    }

    private float getPos(int step) {
        return 1f / (getStepAmount() - 1) * step;
    }

    public DLabelScroll setLabels(List<ILabel> labels) {
        this.labels = labels;
        scroll.setCurrent(0);
        return this;
    }

    private int getStepAmount() {
        int line = (labels.size() + column - 1) / column;
        return line - row + 1;
    }
}
