package me.towdium.jecalculation.nei;

import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import me.towdium.jecalculation.gui.guis.GuiEditor;

public class NEIPlugin {
    public static void init() {
        GuiContainerManager.addTooltipHandler(new JecaTooltipHandler());
    }
}
