package pers.towdium.just_enough_calculation.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:  Towdium
 * Created: 2016/6/13.
 */
public abstract class JECContainer extends Container {
    InventoryBasic inventory;
    List<int[]> slotBuffer = new ArrayList<>();

    {
        windowId = 100;
    }

    public JECContainer() {
        addSlots();
        int count = 0;
        int current = -1;
        for (int[] record : slotBuffer) {
            count += (record[4] * record[5]);
        }
        inventory = new InventoryBasic("Temp", false, count);
        for (int[] record : slotBuffer) {
            for (int a = 0; a < record[4]; a++) {
                for (int b = 0; b < record[5]; b++) {
                    addSlotToContainer(new Slot(inventory, ++current, record[0] + b * record[2], record[1] + a * record[3]));
                }
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

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId >= 0 && getSlotType(slotId) == EnumSlotType.AMOUNT) {
            ItemStack itemStack = getSlot(slotId).getStack();
            if (itemStack != null) {
                itemStack = itemStack.copy();
                if (dragType == 0 && clickTypeIn == ClickType.PICKUP) {
                    itemStack = ItemStackHelper.Click.leftClick(itemStack);
                } else if (dragType == 0 && clickTypeIn == ClickType.QUICK_MOVE) {
                    itemStack = ItemStackHelper.Click.leftShift(itemStack);
                } else if (dragType == 1 && clickTypeIn == ClickType.PICKUP) {
                    itemStack = ItemStackHelper.Click.rightClick(itemStack);
                } else if (dragType == 1 && clickTypeIn == ClickType.QUICK_MOVE) {
                    itemStack = ItemStackHelper.Click.rightShift(itemStack);
                }
                getSlot(slotId).putStack(itemStack);
                return itemStack;
            }
        }
        return slotId > 0 ? getSlot(slotId).getStack() : null;
    }

    @SuppressWarnings("SameParameterValue")
    protected void addSlotSingle(int left, int top) {
        slotBuffer.add(new int[]{left, top, 0, 0, 1, 1});
    }

    protected void addSlotGroup(int left, int top, int intervalH, int intervalV, int sizeH, int sizeV) {
        slotBuffer.add(new int[]{left, top, intervalH, intervalV, sizeH, sizeV});
    }

    protected abstract void addSlots();

    public abstract EnumSlotType getSlotType(int index);

    public enum EnumSlotType {SELECT, AMOUNT, DISABLED, PICKER}
}
