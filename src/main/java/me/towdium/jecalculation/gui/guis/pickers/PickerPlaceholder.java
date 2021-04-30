package me.towdium.jecalculation.gui.guis.pickers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.gui.IWPicker;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.drawables.*;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-29.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class PickerPlaceholder extends IWPicker.Impl {
    public PickerPlaceholder() {
        WLabelScroll scroll = new WLabelScroll(7, 69, 8, 5, WLabel.enumMode.PICKER, true)
                .setLabels(LPlaceholder.getRecent());
        WTextField search = new WTextField(26, 45, 90);
        WTextField create = new WTextField(26, 7, 69);
        create.setLsnrText(s -> create.setColor(s.equals("") ? JecaGui.COLOR_TEXT_RED : JecaGui.COLOR_TEXT_WHITE));
        add(new WSearch(l -> callback.accept(l), search, scroll));
        add(new WIcon(149, 45, 20, 20, Resource.ICN_HELP_N, Resource.ICN_HELP_F, "picker_placeholder.help_search"));
        add(new WIcon(149, 7, 20, 20, Resource.ICN_HELP_N, Resource.ICN_HELP_F, "picker_placeholder.help_create"));
        add(new WIcon(7, 45, 20, 20, Resource.ICN_TEXT_N, Resource.ICN_TEXT_F, "picker_placeholder.text_search"));
        add(new WIcon(7, 7, 20, 20, Resource.ICN_TEXT_N, Resource.ICN_TEXT_F, "picker_placeholder.text_create"));
        add(new WLine(36));
        add(new WButtonIcon(95, 7, 20, 20, Resource.BTN_YES_N, Resource.BTN_YES_F).setListenerLeft(() -> {
            if (!create.getText().equals("")) callback.accept(new LPlaceholder(create.getText(), 1));
        }));
        add(create);
    }
}
