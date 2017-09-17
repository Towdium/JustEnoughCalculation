package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.drawables.DContainer;
import me.towdium.jecalculation.client.gui.drawables.DScroll;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
public class GuiDisambiguation extends DContainer {
    public GuiDisambiguation(List<List<ItemStack>> iss) {
        add(new DScroll(5, 5, 60));
        //add(new DTextField(7, 7, 60));
        //add(new DIcon(149, 7, 20, 20, Resource.ICN_HELP_N, Resource.ICN_HELP_F, "disambiguation.help"));
    }
}
