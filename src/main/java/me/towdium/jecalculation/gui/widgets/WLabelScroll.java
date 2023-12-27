package me.towdium.jecalculation.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.Utilities.I18n;

/**
 * Author: towdium
 * Date: 17-9-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WLabelScroll extends WContainer implements ISearchable {

    protected List<ILabel> labels = new ArrayList<>();
    protected List<ILabel> filtered = null;
    protected WLabelGroup labelGroup;
    protected WScroll scroll;
    protected String filter = "";
    protected int xPos, yPos, column, row, current;
    private ListenerValue<? super WLabelScroll, Integer> lsnrUpdate;
    private ListenerValue<? super WLabelScroll, Integer> hdlrLeftClick, hdlrRightClick;
    protected final boolean accept;

    public WLabelScroll(int xPos, int yPos, int column, int row, boolean accept) {
        this.accept = accept;
        this.xPos = xPos;
        this.yPos = yPos;
        this.column = column;
        this.row = row;
        labelGroup = new WLabelGroup(xPos, yPos, column, row, accept).setLsnrUpdate(this::onUpdate)
            .setLsnrLeftClick(this::onLeftClick)
            .setLsnrRightClick(this::onRightClick);
        scroll = new WScroll(xPos + column * 18 + 4, yPos, row * 18).setListener(i -> update(i.getCurrent()))
            .setStep(Float.POSITIVE_INFINITY)
            .setRatio(1);
        add(labelGroup);
        add(scroll);
        add(new WRectangle(xPos + column * 18, yPos, 4, row * 18, JecaGui.COLOR_GUI_GREY));
    }

    public void update(float f) {
        if (f < 0) f = 0;
        if (f > 1) f = 1;
        int amount = getAmountSteps();
        current = (int) (amount * f);
        if (current == amount) current--;
        List<ILabel> ls = accept ? labels : filtered;
        labelGroup.setLabel(ls, current * column);
        float step = 1f / (amount - 1);
        scroll.setRatio(Math.min(row / (float) getAmountRows(), 1f))
            .setCurrent(f)
            .setStep(step);
    }

    public WLabel get(int index) {
        return labelGroup.get(index - column * current);
    }

    public WLabelScroll setLabels(List<ILabel> labels) {
        this.labels = labels;
        if (accept) update(0);
        else setFilter(filter);
        return this;
    }

    public WLabelScroll setLabel(int idx, ILabel label) {
        labels.set(idx, label);
        return setLabels(labels);
    }

    public boolean setFilter(String str) {
        if (accept) throw new RuntimeException("Filtering not allowed when editing");
        filtered = labels.stream()
            .filter(
                l -> I18n.contains(
                    l.getDisplayName()
                        .toLowerCase(),
                    str.toLowerCase()))
            .collect(Collectors.toList());
        update(0);
        return filtered.size() != 0;
    }

    public WLabelScroll setLsnrUpdate(ListenerValue<? super WLabelScroll, Integer> lsnrUpdate) {
        this.lsnrUpdate = lsnrUpdate;
        return this;
    }

    public WLabelScroll setLsnrLeftClick(ListenerValue<? super WLabelScroll, Integer> hdlrLeftClick) {
        this.hdlrLeftClick = hdlrLeftClick;
        return this;
    }

    public WLabelScroll setLsnrRightClick(ListenerValue<? super WLabelScroll, Integer> hdlrRightClick) {
        this.hdlrRightClick = hdlrRightClick;
        return this;
    }

    public List<ILabel> getLabels() {
        return new ArrayList<>(labels);
    }

    protected void onUpdate(WLabelGroup w, int index) {
        ILabel l = w.get(index)
            .getLabel();
        int i = column * current + index;
        while (labels.size() <= i) labels.add(ILabel.EMPTY);
        labels.set(i, l);
        if (lsnrUpdate != null) lsnrUpdate.invoke(this, i);

        if (index + 1 == row * column && l != ILabel.EMPTY && (row + current) * column == labels.size()) {
            labels.add(ILabel.EMPTY);
            int amount = getAmountSteps();
            float step = 1f / (amount - 1);
            update(step * current);
        }
    }

    protected void onLeftClick(WLabelGroup w, int index) {
        if (hdlrLeftClick != null) hdlrLeftClick.invoke(this, column * current + index);
    }

    protected void onRightClick(WLabelGroup w, int index) {
        if (hdlrRightClick != null) hdlrRightClick.invoke(this, column * current + index);
    }

    public WLabelScroll setLsnrScroll(ListenerValue<? super WLabel, Integer> hdlr) {
        labelGroup.setLsnrScroll(hdlr);
        return this;
    }

    public WLabelScroll setFmtAmount(Function<ILabel, String> f) {
        labelGroup.setFmtAmount(f);
        return this;
    }

    public WLabelScroll setFmtTooltip(BiConsumer<ILabel, List<String>> f) {
        labelGroup.setFmtTooltip(f);
        return this;
    }

    protected int getAmountSteps() {
        return Math.max(getAmountRows() - row + 1, 1);
    }

    protected int getAmountRows() {
        List<ILabel> ls = accept ? labels : filtered;
        return (ls.size() + column - 1) / column;
    }
}
