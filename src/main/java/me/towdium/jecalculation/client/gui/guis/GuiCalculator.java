package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.widgets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static me.towdium.jecalculation.client.widget.widgets.WEntry.enumMode.*;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
public class GuiCalculator extends JecGui {
    public GuiCalculator(@Nullable GuiScreen parent) {
        super(parent, true);
        wgtMgr.add(new WButtonIcon(7, 7, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "label"));
        wgtMgr.add(new WButtonIcon(130, 7, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "recipe")
                           .setListenerLeft(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiEditor(this))));
        wgtMgr.add(new WButtonIcon(149, 7, 20, 20, Resource.BTN_SEARCH_N, Resource.BTN_SEARCH_F, "search"));
        wgtMgr.add(new WEntryGroup(7, 87, 9, 4, RESULT));
        wgtMgr.add(new WEntryGroup(7, 31, 8, 1, PICKER));
        wgtMgr.add(new WEntry(31, 7, 20, 20, SELECTOR));
        wgtMgr.add(new WLine(52));
        wgtMgr.add(new WTextField(61, 7, 64));
        wgtMgr.add(new WIcon(151, 31, 18, 18, Resource.ICN_RECENT_N, Resource.ICN_RECENT_F, "history"));
        wgtMgr.add(new WPager(7, 56, 162, 5));
    }
}
