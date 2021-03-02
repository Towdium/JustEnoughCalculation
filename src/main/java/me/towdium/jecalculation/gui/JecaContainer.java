package me.towdium.jecalculation.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class JecaContainer extends Container {
    InventoryBasic inventory;
    List<int[]> slotBuffer = new ArrayList<>();

    public JecaContainer() {
        addSlots();
        int count = 0;
        int current = -1;
        for(int[] record: slotBuffer) {
            count += (record[4] * record[5]);
        }
        inventory = new InventoryBasic("Temp", false, count);
        for(int[] record: slotBuffer) {
            for(int a = 0; a < record[4]; a++) {
                for(int b=0; b < record[5];b++) {
                    addSlotToContainer(new Slot(inventory, ++current, record[0]+b*record[2], record[1]+a*record[3]));
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return null;
    }

    protected void addSlotSingle(int left, int top){
        slotBuffer.add(new int[]{left, top, 0, 0, 1, 1});
    }

    protected void addSlotGroup(int left, int top, int intervalH, int intervalV, int sizeH, int sizeV){
        slotBuffer.add(new int[]{left, top, intervalH, intervalV, sizeH, sizeV});
    }


    protected abstract void addSlots();
}
