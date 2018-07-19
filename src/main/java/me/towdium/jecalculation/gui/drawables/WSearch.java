package me.towdium.jecalculation.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
@SideOnly(Side.CLIENT)
public class WSearch extends WContainer {
    Consumer<ILabel> clbk;

    public WSearch(Consumer<ILabel> callback, WTextField tf, WLabelScroll... lss) {
        clbk = callback;
        List<WLabelScroll> lst = Arrays.asList(lss);
        tf.setLsnrText(s -> tf.setColor(lst.stream().anyMatch(ls -> ls.setFilter(s)) ?
                JecaGui.COLOR_TEXT_WHITE : JecaGui.COLOR_TEXT_RED));
        add(tf);
        addAll(lss);
        for (WLabelScroll i : lss)
            i.setLsnrUpdate(j -> {
                if (clbk != null) clbk.accept(i.getLabelAt(j));
            });
    }
}
