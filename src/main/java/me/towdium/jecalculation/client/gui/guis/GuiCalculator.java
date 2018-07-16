package me.towdium.jecalculation.client.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;
import me.towdium.jecalculation.data.ControllerClient;
import me.towdium.jecalculation.data.label.ILabel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static me.towdium.jecalculation.client.gui.drawables.WLabel.enumMode.*;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiCalculator extends WContainer {
    WLabelGroup wRecent = new WLabelGroup(7, 31, 8, 1, PICKER);
    WLabel wLabel = new WLabel(31, 7, 20, 20, SELECTOR);

    public GuiCalculator() {
        add(new WPanel());
        add(new WTextField(61, 7, 64));
        add(new WButtonIcon(7, 7, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "calculator.label")
                .setListenerLeft(() -> JecGui.displayGui(new GuiLabel(l -> {
                    JecGui.displayParent();
                    JecGui.getCurrent().hand = l;
                }))));
        add(new WButtonIcon(130, 7, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "calculator.recipe")
                .setListenerLeft(() -> JecGui.displayGui(true, true, new GuiRecipe())));
        add(new WButtonIcon(149, 7, 20, 20, Resource.BTN_SEARCH_N, Resource.BTN_SEARCH_F, "calculator.search"));
        add(new WLabelGroup(7, 87, 9, 4, RESULT));
        add(wRecent);
        add(wLabel);
        add(new WLine(52));
        add(new WIcon(151, 31, 18, 18, Resource.ICN_RECENT_N, Resource.ICN_RECENT_F, "calculator.history"));
        add(new WSwitcher(7, 56, 162, 5));
        refresh();
        List<ILabel> recent = ControllerClient.getRecent();
        if (recent.size() > 0) wLabel.setLabel(ControllerClient.getRecent().get(0));
        wLabel.setLsnrUpdate(() -> {
            ControllerClient.setRecent(wLabel.label);
            refresh();
        });
    }

    void refresh() {
        List<ILabel> recent = ControllerClient.getRecent();
        if (recent.size() > 1) wRecent.setLabel(recent.subList(1, recent.size()), 0);
    }
}
