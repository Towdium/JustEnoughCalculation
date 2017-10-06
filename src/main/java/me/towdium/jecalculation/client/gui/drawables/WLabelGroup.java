package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IWidget;
import me.towdium.jecalculation.core.label.ILabel;
import me.towdium.jecalculation.utils.wrappers.Single;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WLabelGroup extends WContainer {
    ArrayList<WLabel> labels = new ArrayList<>();

    public WLabelGroup(int xPos, int yPos, int column, int row, WLabel.enumMode mode) {
        this(xPos, yPos, column, row, 18, 18, mode);
    }

    public WLabelGroup(int xPos, int yPos, int column, int row, int xSize, int ySize, WLabel.enumMode mode) {
        IntStream.range(0, row).forEach(r -> IntStream.range(0, column).forEach(c -> {
            WLabel l = new WLabel(xPos + c * xSize, yPos + r * ySize, xSize, ySize, mode);
            labels.add(l);
            add(l);
        }));
    }

    @Override
    public void remove(IWidget w) {
        super.remove(w);
        if (w instanceof WLabel) labels.remove(w);
    }

    public Optional<WLabel> getLabelAt(int xMouse, int yMouse) {
        return labels.stream().filter(w -> w.mouseIn(xMouse, yMouse)).findFirst();
    }

    public void setLabel(List<ILabel> labels, int start) {
        Single<Integer> i = new Single<>(start);
        this.labels.forEach(l -> l.setLabel(i.value < labels.size() ? labels.get(i.value++) : ILabel.EMPTY));
    }
}
