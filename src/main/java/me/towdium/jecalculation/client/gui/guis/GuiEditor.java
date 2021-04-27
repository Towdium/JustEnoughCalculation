package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.widgets.*;
import net.minecraft.client.gui.GuiScreen;

import javax.annotation.Nullable;

import static me.towdium.jecalculation.client.widget.widgets.WEntry.enumMode.EDITOR;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
public class GuiEditor extends JecGui {
    WButtonIcon buttonSave = new WButtonIcon(26, 33, 20, 20, Resource.BTN_SAVE_N, Resource.BTN_SAVE_F, "save");
    WButtonIcon buttonCopy = new WButtonIcon(83, 33, 20, 20, Resource.BTN_COPY_N, Resource.BTN_COPY_F, "copy");
    WButtonIcon buttonDel = new WButtonIcon(64, 33, 20, 20, Resource.BTN_DEL_N, Resource.BTN_DEL_F, "clear");
    WButtonIcon buttonLabel = new WButtonIcon(45, 33, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "label");
    WButtonIcon buttonYes = new WButtonIcon(7, 33, 20, 20, Resource.BTN_YES_N, Resource.BTN_YES_F, "confirm");
    WButtonIcon buttonNo = new WButtonIcon(26, 33, 20, 20, Resource.BTN_NO_N, Resource.BTN_NO_F, "abort");
    WEntryGroup groupInput = new WEntryGroup(28, 111, 7, 2, 20, 20, EDITOR);
    WEntryGroup groupCatalyst = new WEntryGroup(28, 87, 7, 1, 20, 20, EDITOR);
    WEntryGroup groupOutput = new WEntryGroup(28, 63, 7, 1, 20, 20, EDITOR);
    WTextField textField = new WTextField(49, 33, 119);
    WButtonIcon buttonNew = new WButtonIcon(7, 33, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "new")
            .setListenerLeft(() -> setModeNewGroup(true));

    public GuiEditor(@Nullable GuiScreen parent) {
        super(parent);
        wgtMgr.add(new WPager(7, 7, 162, 2));
        wgtMgr.add(new WIcon(7, 63, 21, 20, Resource.ICN_OUTPUT_N, Resource.ICN_OUTPUT_F, "output"));
        wgtMgr.add(new WIcon(7, 87, 21, 20, Resource.ICN_CATALYST_N, Resource.ICN_CATALYST_F, "catalyst"));
        wgtMgr.add(new WIcon(7, 111, 21, 40, Resource.ICN_INPUT_N, Resource.ICN_INPUT_F, "input"));
        wgtMgr.add(new WLine(57));
        wgtMgr.addAll(groupInput, groupCatalyst, groupOutput);
        setModeNewGroup(false);
    }

    public void setModeNewGroup(boolean b) {
        if (b) {
            wgtMgr.removeAll(buttonNew, buttonLabel, buttonDel, buttonCopy, buttonSave);
            wgtMgr.addAll(buttonYes, buttonNo, textField);
        } else {
            wgtMgr.addAll(buttonNew, buttonLabel, buttonDel, buttonSave); // TODO buttonCopy
            wgtMgr.removeAll(buttonYes, buttonNo, textField);
        }
    }
}
