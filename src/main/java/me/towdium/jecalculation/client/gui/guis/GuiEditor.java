package me.towdium.jecalculation.client.gui.guis;

import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;

import static me.towdium.jecalculation.client.gui.drawables.DEntry.enumMode.EDITOR;

/**
 * Author: towdium
 * Date:   17-9-8.
 */
public class GuiEditor extends DContainer {
    DButton buttonSave = new DButton(26, 33, 20, 20, Resource.BTN_SAVE_N, Resource.BTN_SAVE_F, "save");
    DButton buttonCopy = new DButton(83, 33, 20, 20, Resource.BTN_COPY_N, Resource.BTN_COPY_F, "copy");
    DButton buttonDel = new DButton(64, 33, 20, 20, Resource.BTN_DEL_N, Resource.BTN_DEL_F, "clear");
    DButton buttonLabel = new DButton(45, 33, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "label");
    DButton buttonYes = new DButton(7, 33, 20, 20, Resource.BTN_YES_N, Resource.BTN_YES_F, "confirm");
    DButton buttonNo = new DButton(26, 33, 20, 20, Resource.BTN_NO_N, Resource.BTN_NO_F, "abort");
    DEntryGroup groupInput = new DEntryGroup(28, 111, 7, 2, 20, 20, EDITOR);
    DEntryGroup groupCatalyst = new DEntryGroup(28, 87, 7, 1, 20, 20, EDITOR);
    DEntryGroup groupOutput = new DEntryGroup(28, 63, 7, 1, 20, 20, EDITOR);
    DTextField textField = new DTextField(49, 33, 119);
    DButton buttonNew = new DButton(7, 33, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "new")
            .setListenerLeft(() -> setModeNewGroup(true));

    public GuiEditor() {
        add(new DPager(7, 7, 162, 2));
        add(new DIcon(7, 63, 21, 20, Resource.ICN_OUTPUT_N, Resource.ICN_OUTPUT_F, "output"));
        add(new DIcon(7, 87, 21, 20, Resource.ICN_CATALYST_N, Resource.ICN_CATALYST_F, "catalyst"));
        add(new DIcon(7, 111, 21, 40, Resource.ICN_INPUT_N, Resource.ICN_INPUT_F, "input"));
        add(new DLine(57));
        addAll(groupInput, groupCatalyst, groupOutput);
        setModeNewGroup(false);
    }

    public void setModeNewGroup(boolean b) {
        if (b) {
            removeAll(buttonNew, buttonLabel, buttonDel, buttonCopy, buttonSave);
            addAll(buttonYes, buttonNo, textField);
        } else {
            addAll(buttonNew, buttonLabel, buttonDel, buttonSave); // TODO buttonCopy
            removeAll(buttonYes, buttonNo, textField);
        }
    }
}
