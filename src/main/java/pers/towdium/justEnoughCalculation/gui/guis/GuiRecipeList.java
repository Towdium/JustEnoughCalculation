package pers.towdium.justEnoughCalculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import pers.towdium.justEnoughCalculation.gui.JECContainer;
import pers.towdium.justEnoughCalculation.gui.JECGuiContainer;

/**
 * Author:  Towdium
 * Created: 2016/6/15.
 */
public abstract class GuiRecipeList extends JECGuiContainer {
    public GuiRecipeList(Container inventorySlotsIn, GuiScreen parent) {
        super(inventorySlotsIn, parent);
    }

    @Override
    protected int getSizeSlot(int index) {
        return 0;
    }

    @Override
    public void initGui() {
        super.initGui();
        for(int i=0; i<5; i++){
            buttonList.add(new GuiButtonExt(2*i, guiLeft+83, guiTop+32+21*i, 41, 18,"edit"));
            buttonList.add(new GuiButtonExt(1+2*i, guiLeft+128, guiTop+32+21*i, 41, 18, "delete"));
        }
        buttonList.add(new GuiButtonExt(10, guiLeft+7, guiTop+139, 20, 20, "<"));
        buttonList.add(new GuiButtonExt(11, guiLeft+149, guiTop+139, 20, 20, ">"));
    }

    protected static abstract class ContainerRecipeList extends JECContainer{
        @Override
        protected void addSlots() {
            addSlotGroup(8, 33, 18, 21, 5, 4);
        }
    }
}
