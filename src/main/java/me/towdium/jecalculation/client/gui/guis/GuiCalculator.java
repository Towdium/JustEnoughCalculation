package me.towdium.jecalculation.client.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;

import javax.annotation.ParametersAreNonnullByDefault;

import static me.towdium.jecalculation.client.gui.drawables.DEntry.enumMode.*;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GuiCalculator extends DContainer {
    public GuiCalculator() {
        add(new DTextField(61, 7, 64));
        add(new DButton(7, 7, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "label"));
        add(new DButton(130, 7, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "recipe")
                .setListenerLeft(() -> JecGui.displayGui(new GuiEditor())));
        add(new DButton(149, 7, 20, 20, Resource.BTN_SEARCH_N, Resource.BTN_SEARCH_F, "search"));
        add(new DEntryGroup(7, 87, 9, 4, RESULT));
        add(new DEntryGroup(7, 31, 8, 1, PICKER));
        add(new DEntry(31, 7, 20, 20, SELECTOR));
        add(new DLine(52));
        add(new DIcon(151, 31, 18, 18, Resource.ICN_RECENT_N, Resource.ICN_RECENT_F, "history"));
        add(new DPager(7, 56, 162, 5));
    }
}
