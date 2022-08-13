package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;

import static me.towdium.jecalculation.gui.Resource.WGT_CROSS_F;
import static me.towdium.jecalculation.gui.Resource.WGT_CROSS_N;

public class WCross extends WButton {
    public WCross(int xPos, int yPos) {
        super(xPos, yPos, 5, 5, null);
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        boolean hovered = hovered(xMouse, yMouse);
        Resource res = hovered ? WGT_CROSS_F : WGT_CROSS_N;
        gui.drawResource(res, xPos, yPos);
        return hovered;
    }
}
