package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.guis.pickers.IPicker;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.gui.widgets.WLabel.Mode;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static me.towdium.jecalculation.gui.Resource.*;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiDisamb extends IPicker.Impl implements IGui {
    protected WLabelScroll up = new WLabelScroll(25, 48, 7, 3, Mode.PICKER, true);
    protected WLabelScroll down = new WLabelScroll(25, 105, 7, 3, Mode.PICKER, true);
    protected List<List<ILabel>> record;

    public GuiDisamb(List<List<ILabel>> record) {
        this.record = record;
        WSwitcher switcher = new WSwitcher(7, 7, 162, this.record.size()).setListener(i -> setPage(i.getIndex()));
        ListenerValue<WLabelScroll, Integer> consumer = (i, v) -> callback.accept(i.get(v).copy().multiply(-1));
        add(new WPanel());
        add(new WIcon(7, 24, 20, 20, ICN_TEXT, "common.search"));
        add(new WIcon(7, 48, 18, 54, ICN_LIST, "disamb.list"));
        add(new WIcon(7, 105, 18, 54, ICN_LABEL, "disamb.label"));
        add(new WSearch(25, 24, 90, up.setListener(consumer), down.setListener(consumer)));
        add(switcher, up, down);
        setPage(0);
    }

    protected void setPage(int n) {
        up.setLabels(record.get(n));
        down.setLabels(ILabel.CONVERTER.guess(record.get(n)).one);
    }
}
