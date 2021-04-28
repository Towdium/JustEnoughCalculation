package me.towdium.jecalculation.client.gui.drawables;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.Utilities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-17.
 */
@ParametersAreNonnullByDefault
public class DLabelScroll extends DContainer {
    protected List<ILabel> labels = new ArrayList<>();
    protected List<ILabel> filtered = new ArrayList<>();
    protected DLabelGroup labelGroup;
    protected DScroll scroll;
    protected int xPos, yPos, column, row, current;
    protected String filter = "";

    public DLabelScroll(int xPos, int yPos, int column, int row, DLabel.enumMode mode, boolean drawConnection) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.column = column;
        this.row = row;
        labelGroup = new DLabelGroup(xPos, yPos, column, row, mode);
        scroll = new DScroll(xPos + column * 18 + 4, yPos, row * 18).setLsnrScroll(this::update);
        add(labelGroup);
        add(scroll);
        if (drawConnection) add(new DRectangle(xPos + column * 18, yPos, 4, row * 18, JecGui.COLOR_GUI_GREY));
    }

    public void update(float f) {
        int step = getStepAmount();
        current = (int) (step * f);
        if (current == step) current--;
        labelGroup.setLabel(filtered, current * column);
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
        setFilter(filter);
        return this;
    }

    public boolean setFilter(String str) {
        filter = str;
        filtered = labels.stream().filter(l -> Utilities.contains(l.getDisplayName(), str))
                         .collect(Collectors.toList());
        scroll.setCurrent(0);
        return filtered.size() != 0;
    }

    private int getStepAmount() {
        int line = (labels.size() + column - 1) / column;
        return line - row + 1;
    }
}
