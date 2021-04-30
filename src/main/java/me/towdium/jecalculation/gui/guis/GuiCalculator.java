package me.towdium.jecalculation.gui.guis;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.drawables.*;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.Controller;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiCalculator extends WContainer {
    WLabelGroup wRecent = new WLabelGroup(7, 31, 8, 1, WLabel.enumMode.PICKER);
    WLabel wLabel = new WLabel(31, 7, 20, 20, WLabel.enumMode.SELECTOR);
    WButton btnSteps = new WButtonIcon(64, 62, 20, 20, Resource.BTN_LIST_N, Resource.BTN_LIST_F,
                                       Resource.BTN_LIST_D, "calculator.step").setListenerLeft(() -> setMode(enumMode.STEPS));
    WButton btnCatalyst = new WButtonIcon(45, 62, 20, 20, Resource.BTN_CAT_N, Resource.BTN_CAT_F,
                                          Resource.BTN_CAT_D, "calculator.catalyst").setListenerLeft(() -> setMode(enumMode.CATALYST));
    WButton btnOutput = new WButtonIcon(26, 62, 20, 20, Resource.BTN_OUT_N, Resource.BTN_OUT_F,
                                        Resource.BTN_OUT_D, "calculator.output").setListenerLeft(() -> setMode(enumMode.OUTPUT));
    WButton btnInput = new WButtonIcon(7, 62, 20, 20, Resource.BTN_IN_N, Resource.BTN_IN_F,
                                       Resource.BTN_IN_D, "calculator.input").setListenerLeft(() -> setMode(enumMode.INPUT));

    public GuiCalculator() {
        wLabel.setLsnrUpdate(() -> {
            Controller.setRecent(wLabel.label);
            refreshRecent();
        });
        wRecent.setLsnrUpdate(l -> JecaGui.getCurrent().hand = wRecent.getLabelAt(l));
        add(new WPanel());
        add(new WTextField(61, 7, 64));
        add(new WButtonIcon(7, 7, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "calculator.label")
                    .setListenerLeft(() -> JecaGui.displayGui(new GuiLabel(l -> {
                        JecaGui.displayParent();
                        JecaGui.getCurrent().hand = l;
                    }))));
        add(new WButtonIcon(130, 7, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "calculator.recipe")
                    .setListenerLeft(() -> JecaGui.displayGui(true, true, new GuiRecipe())));
        add(new WButtonIcon(149, 7, 20, 20, Resource.BTN_SEARCH_N, Resource.BTN_SEARCH_F, "calculator.search")
                    .setListenerLeft(() -> JecaGui.displayGui(new GuiSearch())));
        add(new WLine(55));
        add(new WIcon(151, 31, 18, 18, Resource.ICN_RECENT_N, Resource.ICN_RECENT_F, "calculator.history"));
        add(new WLabelScroll(7, 87, 8, 4, WLabel.enumMode.RESULT, true));
        addAll(wRecent, wLabel, btnInput, btnOutput, btnCatalyst, btnSteps);
        refreshRecent();
        setMode(enumMode.INPUT);
    }

    void setMode(enumMode mode) {
        btnInput.setDisabled(mode == enumMode.INPUT);
        btnOutput.setDisabled(mode == enumMode.OUTPUT);
        btnCatalyst.setDisabled(mode == enumMode.CATALYST);
        btnSteps.setDisabled(mode == enumMode.STEPS);
    }

    void refreshRecent() {
        List<ILabel> recent = Controller.getRecent();
        if (recent.size() > 1) wRecent.setLabel(recent.subList(1, recent.size()), 0);
    }

    enum enumMode {
        INPUT, OUTPUT, CATALYST, STEPS
    }
}
