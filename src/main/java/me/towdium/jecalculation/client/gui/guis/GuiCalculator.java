package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.widgets.WButtonIcon;
import me.towdium.jecalculation.client.widget.widgets.WEntryGroup;
import me.towdium.jecalculation.client.widget.widgets.WLine;
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
        widgetManager.add(new WButtonIcon(7, 7, Resource.LABEL_NORMAL, Resource.LABEL_FOCUSED)
                .setListenerLeft(() -> Minecraft.getMinecraft().displayGuiScreen(null))
                .setListenerRight(() -> Minecraft.getMinecraft().displayGuiScreen(null)));
        widgetManager.add(new WButtonIcon(130, 7, Resource.NEW_NORMAL, Resource.NEW_FOCUSED));
        widgetManager.add(new WButtonIcon(149, 7, Resource.SEARCH_NORMAL, Resource.SEARCH_FOCUSED));
        widgetManager.add(new WEntryGroup(7, 87, 9, 4));
        widgetManager.add(new WLine(52));
    }

    @Override
    protected String getBackground() {
        return "gui_calculator";
    }
}
