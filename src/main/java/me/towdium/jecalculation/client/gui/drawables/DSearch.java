package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.core.labels.ILabel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DSearch extends DContainer {
    DTextField tf;
    List<DLabelScroll> lss;
    Consumer<ILabel> callback;

    public DSearch(Consumer<ILabel> callback, DTextField tf, DLabelScroll... lss) {
        List<DLabelScroll> lst = Arrays.asList(lss);
        tf.setLsnrText(s -> tf.setColor(lst.stream().anyMatch(ls -> ls.setFilter(s)) ?
                JecGui.COLOR_TEXT_WHITE : JecGui.COLOR_TEXT_RED));
        add(tf);
        addAll(lss);
        this.tf = tf;
        this.lss = lst;
        this.callback = callback;
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        boolean ret = super.onClicked(gui, xMouse, yMouse, button);
        if (gui.hand != ILabel.EMPTY) callback.accept(gui.hand);
        return ret;
    }
}
