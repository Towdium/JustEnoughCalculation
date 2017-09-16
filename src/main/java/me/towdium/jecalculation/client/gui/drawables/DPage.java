package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.helpers.LocalizationHelper;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DPage implements IDrawable {


    protected int index;
    protected ILabel.RegistryEditor.Record record;
    protected Utilities.Timer timer = new Utilities.Timer();
    protected boolean focused;
    protected Runnable listener;

    public DPage(int index, ILabel.RegistryEditor.Record record, boolean focused) {
        this.index = index;
        this.record = record;
        this.focused = focused;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        Resource resource = focused ? (index == 0 ? Resource.WGT_PAGER_F0 : Resource.WGT_PAGER_FN)
                : Resource.WGT_PAGER_N;
        gui.drawResourceContinuous(resource, index * 24, -21, 24, 25, 4, 4, 4, 4);
        record.representation.drawEntry(gui, index * 24 + 4, -17, false);
        timer.setState(JecGui.mouseIn(index * 24, -21, 24, 21, xMouse, yMouse));
        if (timer.getTime() > 1000)
            gui.drawTooltip(xMouse, yMouse, LocalizationHelper.format("gui." + record.localizeKey));
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        boolean ret = JecGui.mouseIn(index * 24, -21, 24, 21, xMouse, yMouse) && listener != null && !focused;
        if (ret) listener.run();
        return ret;
    }

    public DPage setListener(Runnable r) {
        listener = r;
        return this;
    }
}
