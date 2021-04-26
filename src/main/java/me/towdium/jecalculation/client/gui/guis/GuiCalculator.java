package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.widgets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
public class GuiCalculator extends JecGui {
    public GuiCalculator(@Nullable GuiScreen parent) {
        super(parent);
        widgetManager.add(new WButtonIcon(7, 7, 20, 20, Resource.BUTTON_LABEL_N, Resource.BUTTON_LABEL_F)
                                  .setListenerLeft(() -> Minecraft.getMinecraft().displayGuiScreen(null))
                                  .setListenerRight(() -> Minecraft.getMinecraft().displayGuiScreen(null)));
        widgetManager.add(new WButtonIcon(130, 7, 20, 20, Resource.BUTTON_NEW_N, Resource.BUTTON_NEW_F));
        widgetManager.add(new WButtonIcon(149, 7, 20, 20, Resource.BUTTON_SEARCH_N, Resource.BUTTON_SEARCH_F));
        widgetManager.add(new WEntryGroup(7, 87, 9, 4));
        widgetManager.add(new WEntryGroup(7, 31, 8, 1));
        widgetManager.add(new WEntry(31, 7, 20, 20));
        widgetManager.add(new WLine(52));
        widgetManager.add(new WTextField(62, 7, 63));
        widgetManager.add(new WIcon(151, 31, 18, 18, Resource.ICON_RECENT_N, Resource.ICON_RECENT_F));
        widgetManager.add(new WPager(7, 56, 162));
    }
}
