package pers.towdium.tudicraft.gui.itemPicker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pers.towdium.tudicraft.Tudicraft;


/**
 * @author Towdium
 */
public class ContainerItemPicker extends Container {

    InventoryItemPicker inventoryItemPicker;

    public ContainerItemPicker(){
        inventoryItemPicker = new InventoryItemPicker();
        addSlotToContainer(new Slot(inventoryItemPicker ,0,0,0));
        addSlotToContainer(new Slot(inventoryItemPicker,1,18,0));

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
        Tudicraft.log.info("Activated");
        return null;
    }
}
