package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.widgets.IWidget;
import me.towdium.jecalculation.gui.widgets.WContainer;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class Gui extends WContainer implements IGui {
    protected WContainer overlay = new WContainer();
    protected WContainer widgets = new WContainer();

    public Gui() {
        super.add(widgets);
        super.add(overlay);
    }

    public void setOverlay(IWidget overlay) {
        this.overlay.clear();
        if (overlay != null) this.overlay.add(overlay);
    }

    @Override
    public void add(IWidget... w) {
        widgets.add(w);
    }

    @Override
    public void remove(IWidget... w) {
        widgets.remove(w);
    }

    @Override
    public void clear() {
        widgets.clear();
    }

    @Override
    public boolean contains(IWidget w) {
        return widgets.contains(w);
    }
}
