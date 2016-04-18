package pers.towdium.justEnoughCalculation.gui.guis.recipeViewer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pers.towdium.justEnoughCalculation.core.Recipe;

/**
 * @author Towdium
 */
public class ContainerRecipeViewer extends Container {
    InventoryBasic inventory = new InventoryBasic("RecipeViewer", false, 24);

    public ContainerRecipeViewer(){
        int i, left, top;
        i = 0; left = 8; top = 8;
        for(int a = 0; a < 6; a++){
            for(int b =0; b < 4; b++){
                addSlotToContainer(new Slot(inventory,i+4*a+b,left+b*18,top+a*22));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return null;
    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
        return null;
    }

    @Override
    public Slot getSlot(int p_75139_1_) {
        if(p_75139_1_ <= 23)
            return super.getSlot(p_75139_1_);
        else
            return null;
    }

    @Override
    public void putStackInSlot(int p_75141_1_, ItemStack p_75141_2_) {
    }
}
