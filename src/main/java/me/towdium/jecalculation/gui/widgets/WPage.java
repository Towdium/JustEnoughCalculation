package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.ILabel.RegistryEditor.Record;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WPage implements IWidget {
    protected int index;
    protected ILabel.RegistryEditor.Record record;
    protected Utilities.Timer timer = new Utilities.Timer();
    protected boolean focused;
    protected Runnable listener;

    public WPage(int index, Record record, boolean focused) {
        this.index = index;
        this.record = record;
        this.focused = focused;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        Resource resource = focused ?
                            (index == 0 ? Resource.WGT_PAGER_F0 : Resource.WGT_PAGER_FN) :
                            Resource.WGT_PANEL_N;
        gui.drawResourceContinuous(resource, index * 24, -21, 24, 25, 4, 4, 4, 4);
        record.representation.drawLabel(gui, index * 24 + 4, -17, false);
        timer.setState(JecaGui.mouseIn(index * 24, -21, 24, 21, xMouse, yMouse));
    }

    @Override
    public boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        boolean ret = JecaGui.mouseIn(index * 24, -21, 24, 21, xMouse, yMouse) && listener != null && !focused;
        if (ret)
            listener.run();
        return ret;
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        if (timer.getTime() > 1000 && !focused)
            tooltip.add(Utilities.I18n.format("gui." + record.localizeKey));
        return false;
    }

    public WPage setListener(Runnable r) {
        listener = r;
        return this;
    }
}
