package me.towdium.jecalculation.gui.guis.pickers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.gui.widgets.*;

import javax.annotation.ParametersAreNonnullByDefault;
import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_RED;
import static me.towdium.jecalculation.gui.JecaGui.COLOR_TEXT_WHITE;
import static me.towdium.jecalculation.gui.Resource.BTN_YES;
import static me.towdium.jecalculation.gui.Resource.ICN_TEXT;

/**
 * Author: towdium
 * Date:   17-9-29.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class PickerPlaceholder extends IPicker.Impl implements IGui {
    public PickerPlaceholder() {
        WLabelScroll scroll = new WLabelScroll(7, 69, 8, 5, WLabel.Mode.PICKER, true)
                .setLabels(LPlaceholder.getRecent()).setListener((i, v) -> notifyLsnr(i.get(v)));
        WTextField create = new WTextField(26, 7, 69)
                .setListener(i -> i.setColor(i.getText().equals("") ? COLOR_TEXT_RED : COLOR_TEXT_WHITE));
        add(new WSearch(26, 45, 90, scroll));
        add(new WIcon(7, 45, 20, 20, ICN_TEXT, "common.search"));
        add(new WIcon(7, 7, 20, 20, ICN_TEXT, "placeholder.create"));
        add(new WLine(36));
        add(new WButtonIcon(95, 7, 20, 20, BTN_YES, "common.confirm").setListener(i -> {
            if (!create.getText().equals("")) callback.accept(new LPlaceholder(create.getText(), 1));
        }));
        add(scroll, create);
    }
}
