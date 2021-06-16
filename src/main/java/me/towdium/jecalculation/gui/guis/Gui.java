package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.IWidget;
import me.towdium.jecalculation.gui.widgets.WContainer;
import me.towdium.jecalculation.utils.wrappers.Wrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Gui extends WContainer implements IGui {
    protected List<IWidget> widgets = new ArrayList<>();
    protected IWidget overlay = null;

    public void setOverlay(IWidget overlay) {
        this.overlay = overlay;
    }

    public void clear() {
        widgets.clear();
    }

    public boolean contains(IWidget w) {
        return widgets.contains(w);
    }

    @Override
    public boolean onDraw(JecaGui gui, int mouseX, int mouseY) {
        super.onDraw(gui, mouseX, mouseY);
        if (overlay != null) overlay.onDraw(gui, mouseX, mouseY);
        return false;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        boolean b = overlay != null && overlay.onMouseClicked(gui, xMouse, yMouse, button);
        return b || super.onMouseClicked(gui, xMouse, yMouse, button);
    }

    @Override
    public boolean onKeyPressed(JecaGui gui, int key, int modifier) {
        boolean b = overlay != null && overlay.onKeyPressed(gui, key, modifier);
        return b || super.onKeyPressed(gui, key, modifier);
    }

    @Override
    public boolean onKeyReleased(JecaGui gui, int key, int modifier) {
        boolean b = overlay != null && overlay.onKeyReleased(gui, key, modifier);
        return b || super.onKeyReleased(gui, key, modifier);
    }

    @Override
    public boolean onMouseReleased(JecaGui gui, int xMouse, int yMouse, int button) {
        boolean b = overlay != null && overlay.onMouseReleased(gui, xMouse, yMouse, button);
        return b || super.onMouseReleased(gui, xMouse, yMouse, button);
    }

    @Override
    public boolean onChar(JecaGui gui, char ch, int modifier) {
        boolean b = overlay != null && overlay.onChar(gui, ch, modifier);
        return b || super.onChar(gui, ch, modifier);
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        boolean b = overlay != null && overlay.onMouseScroll(gui, xMouse, yMouse, diff);
        return b || super.onMouseScroll(gui, xMouse, yMouse, diff);
    }

    @Override
    public boolean onMouseDragged(JecaGui gui, int xMouse, int yMouse, int xDrag, int yDrag) {
        boolean b = overlay != null && overlay.onMouseDragged(gui, xMouse, yMouse, xDrag, yDrag);
        return b || super.onMouseDragged(gui, xMouse, yMouse, xDrag, yDrag);
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        boolean b = overlay != null && overlay.onTooltip(gui, xMouse, yMouse, tooltip);
        return b || super.onTooltip(gui, xMouse, yMouse, tooltip);
    }

    @Override
    public boolean getLabelUnderMouse(int xMouse, int yMouse, Wrapper<ILabel> label) {
        boolean b = overlay != null && overlay.getLabelUnderMouse(xMouse, yMouse, label);
        return b || super.getLabelUnderMouse(xMouse, yMouse, label);
    }
}
