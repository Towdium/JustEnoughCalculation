package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.wrappers.Wrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WLabelGroup extends WContainer {
    ArrayList<WLabel> labels = new ArrayList<>();
    Consumer<Integer> lsnrUpdate;

    public WLabelGroup(int xPos, int yPos, int column, int row, WLabel.enumMode mode) {
        this(xPos, yPos, column, row, 18, 18, mode);
    }

    public WLabelGroup(int xPos, int yPos, int column, int row, int xSize, int ySize, WLabel.enumMode mode) {
        IntStream.range(0, row).forEach(r -> IntStream.range(0, column).forEach(c -> {
            WLabel l = new WLabel(xPos + c * xSize, yPos + r * ySize, xSize, ySize, mode).setLsnrUpdate(() -> {
                if (lsnrUpdate != null) lsnrUpdate.accept(r * column + c);
            });
            labels.add(l);
            add(l);
        }));
    }

    public ILabel getLabelAt(int index) {
        return labels.get(index).getLabel();
    }

    public Optional<ILabel> getLabelAt(int xMouse, int yMouse) {
        return labels.stream().filter(w -> w.mouseIn(xMouse, yMouse)).findFirst().map(WLabel::getLabel);
    }

    public List<ILabel> getLabels() {
        return labels.stream().map(WLabel::getLabel).collect(Collectors.toList());
    }

    public void setLabel(ILabel label, int index) {
        labels.get(index).setLabel(label);
    }

    public void setLabel(List<ILabel> labels, int start) {
        for (WLabel label : this.labels) label.setLabel(start < labels.size() ? labels.get(start++) : ILabel.EMPTY);
    }

    public WLabelGroup setLsnrUpdate(Consumer<Integer> listener) {
        lsnrUpdate = listener;
        return this;
    }
}