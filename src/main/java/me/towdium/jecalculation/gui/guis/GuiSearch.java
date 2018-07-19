package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.data.ControllerClient;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.drawables.*;
import me.towdium.jecalculation.utils.Utilities;

import java.util.ArrayList;

public class GuiSearch extends WContainer {
    public GuiSearch() {
        add(new WPanel());
        ArrayList<String> groups = new ArrayList<>();
        groups.add(Utilities.I18n.format("gui.search.all"));
        groups.addAll(ControllerClient.getGroups());
        add(new WSwitcher(7, 7, 162, groups));
        WTextField tf = new WTextField(25, 7, 90);
        WLabelScroll ls = new WLabelScroll(7, 33, 8, 7, WLabel.enumMode.PICKER, true);
        add(new WIcon(149, 7, 20, 20, Resource.ICN_HELP_N, Resource.ICN_HELP_F, "search.help"));
        add(new WIcon(7, 7, 20, 20, Resource.ICN_TEXT_N, Resource.ICN_TEXT_F, "search.text"));
    }
}
