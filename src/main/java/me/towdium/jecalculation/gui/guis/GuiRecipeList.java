package me.towdium.jecalculation.gui.guis;

import cpw.mods.fml.client.config.GuiButtonExt;
import me.towdium.jecalculation.gui.JecaContainer;
import me.towdium.jecalculation.gui.JecaGuiContainer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

public abstract class GuiRecipeList extends JecaGuiContainer {

    public GuiRecipeList(Container inventorySlotsIn, GuiScreen parent) {
        super(inventorySlotsIn, parent);
    }

    @Override
    protected int getSizeSlot(int index) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        for (int i = 0; i < 5; i++) {
            buttonList.add(new GuiButtonExt(2 * i, guiLeft + 83, guiTop + 32 + 21 * i, 41, 18, "edit"));
            buttonList.add(new GuiButtonExt(1 + 2 * i, guiLeft + 128, guiTop + 32 + 21 * i, 41, 18, "delete"));
        }
        buttonList.add(new GuiButtonExt(10, guiLeft + 7, guiTop + 139, 20, 20, "<"));
        buttonList.add(new GuiButtonExt(11, guiLeft + 149, guiTop + 139, 20, 20, ">"));
    }

    protected static abstract class ContainerRecipeList extends JecaContainer {
        @Override
        protected void addSlots() {
            addSlotGroup(8, 33, 18, 21, 5, 4);
        }
    }
}
