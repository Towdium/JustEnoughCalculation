package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;

import javax.annotation.ParametersAreNonnullByDefault;

import static me.towdium.jecalculation.client.gui.drawables.DLabel.enumMode.*;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
public class GuiCalculator extends DContainer {
    public GuiCalculator() {
        add(new DPanel());
        add(new DTextField(61, 7, 64));
        add(new DButtonIcon(7, 7, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "calculator.label")
                    .setListenerLeft(() -> JecGui.displayGui(new GuiLabel(l -> {
                        JecGui.displayGui(this);
                        JecGui.getCurrent().hand = l;
                    }))));
        add(new DButtonIcon(130, 7, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "calculator.recipe")
                    .setListenerLeft(() -> JecGui.displayGui(new GuiRecipe())));
        add(new DButtonIcon(149, 7, 20, 20, Resource.BTN_SEARCH_N, Resource.BTN_SEARCH_F, "calculator.search"));
        add(new DLabelGroup(7, 87, 9, 4, RESULT));
        add(new DLabelGroup(7, 31, 8, 1, PICKER));
        add(new DLabel(31, 7, 20, 20, SELECTOR));
        add(new DLine(52));
        add(new DIcon(151, 31, 18, 18, Resource.ICN_RECENT_N, Resource.ICN_RECENT_F, "calculator.history"));
        add(new DScroll(7, 56, 162, 5));
    }
}
