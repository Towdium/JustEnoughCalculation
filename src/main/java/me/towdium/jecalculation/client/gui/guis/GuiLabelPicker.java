package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.drawables.DContainer;
import me.towdium.jecalculation.client.gui.drawables.DPager;
import me.towdium.jecalculation.client.gui.drawables.DPanel;

/**
 * Author: towdium
 * Date:   17-9-14.
 */
public class GuiLabelPicker extends DContainer {
    public GuiLabelPicker() {
        add(new DPager());
        add(new DPanel());
    }
}
