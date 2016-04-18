package pers.towdium.justEnoughCalculation.gui.guis.calculator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.core.ItemStackWrapper;


/**
 * @author Towdium
 */
public class ContainerCalculator extends Container {
    EntityPlayer player;
    InventoryCalculator inventoryCalculator;

    public ContainerCalculator(EntityPlayer player, ItemStack itemCalculator){
        this.player = player;
        this.inventoryCalculator = new InventoryCalculator(itemCalculator);
        addSlotToContainer(new Slot(inventoryCalculator, 0,9,9));
        int i, left, top;
        i = 1; left = 8; top = 80;
        for(int a = 0; a < 3; a++){
            for(int b =0; b < 9; b++){
                addSlotToContainer(new Slot(inventoryCalculator,i+9*a+b,left+b*18,top+a*18));
            }
        }
        i = 28; left = 8; top = 32;
        for(int a = 0; a < 1; a++){
            for(int b =0; b < 6; b++){
                addSlotToContainer(new Slot(inventoryCalculator,i+9*a+b,left+b*18,top+a*18));
            }
        }
    }

    @Override
    public Slot getSlot(int p_75139_1_) {
        if(p_75139_1_ <= 33)
            return super.getSlot(p_75139_1_);
        else
            return null;
    }

    @Override
    public void putStackInSlot(int p_75141_1_, ItemStack p_75141_2_) {
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

    public EntityPlayer getPlayer() {
        return player;
    }
}
