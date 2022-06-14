package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.data.label.ILabel.RegistryEditor.Record;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static me.towdium.jecalculation.gui.Resource.WGT_PAGER_F;
import static me.towdium.jecalculation.gui.Resource.WGT_PANEL_N;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WPage extends WTooltip {
    protected int index;
    protected Record record;
    protected boolean focused;
    protected ListenerAction<? super WPage> listener;

    public WPage(int index, Record record, boolean focused) {
        super(record.localizeKey);
        this.index = index;
        this.record = record;
        this.focused = focused;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        Resource resource = focused ? WGT_PAGER_F : WGT_PANEL_N;
        gui.drawResourceContinuous(resource, index * 29, getTop(), 28, getHeight(), 4, 4, 4, 4);
        record.representation.drawLabel(gui, index * 29 + 6, -19, false);
        super.onDraw(gui, xMouse, yMouse);
        return false;
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        return super.onTooltip(gui, xMouse, yMouse, tooltip) || mouseIn(xMouse, yMouse);
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (mouseIn(xMouse, yMouse) && listener != null && !focused) {
            listener.invoke(this);
            return true;
        } else return false;
    }

    @Override
    public boolean mouseIn(int xMouse, int yMouse) {
        return JecaGui.mouseIn(index * 29, getTop(), 28, getHeight(), xMouse, yMouse);
    }

    public int getTop() {
        return focused ? -28 : -26;
    }

    public int getHeight() {
        return focused ? 32 : 30;
    }

    public WPage setListener(ListenerAction<? super WPage> r) {
        listener = r;
        return this;
    }
}
