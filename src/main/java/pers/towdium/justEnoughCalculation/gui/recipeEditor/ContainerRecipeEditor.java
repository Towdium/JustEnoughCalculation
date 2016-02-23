package pers.towdium.justEnoughCalculation.gui.recipeEditor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pers.towdium.justEnoughCalculation.core.Recipe;


/**
 * @author Towdium
 */
public class ContainerRecipeEditor extends Container {

    InventoryRecipeEditor inventoryRecipeEditor;
    EntityPlayer player;

    public ContainerRecipeEditor(EntityPlayer player){
        inventoryRecipeEditor = new InventoryRecipeEditor();
        int i, left, top;
        i = 0; left = 9; top = 9;
        for(int a = 0; a < 2; a++){
            for(int b =0; b < 2; b++){
                addSlotToContainer(new Slot(inventoryRecipeEditor,i+2*a+b,left+b*59,top+a*24));
            }
        }
        i = 4; left = 9; top = 69;
        for(int a = 0; a < 4; a++){
            for(int b =0; b < 3; b++){
                addSlotToContainer(new Slot(inventoryRecipeEditor,i+3*a+b,left+b*59,top+a*24));
            }
        }
        this.player = player;
    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
        if(slotId<0 || slotId>=16){
            return null;
        }
        Slot slot = getSlot(slotId);
        if(slot != null){
            ItemStack itemStack = slot.inventory.getStackInSlot(slot.getSlotIndex());
            if(itemStack != null){
                if(clickedButton == 0){
                    ItemStack itemStackBuffer = itemStack.copy();
                    itemStackBuffer.stackSize += 1;
                    slot.inventory.setInventorySlotContents(slot.getSlotIndex(), itemStackBuffer);
                    slot.onSlotChanged();
                }else if(clickedButton == 1){
                    if(itemStack.stackSize == 1){
                        slot.inventory.setInventorySlotContents(slot.getSlotIndex(),null);
                        slot.onSlotChanged();
                    }else {
                        ItemStack itemStackBuffer = itemStack.copy();
                        itemStackBuffer.stackSize -= 1;
                        slot.inventory.setInventorySlotContents(slot.getSlotIndex(), itemStackBuffer);
                        slot.onSlotChanged();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return null;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public Recipe buildRecipe(){
        ItemStack[] output = new ItemStack[4];
        boolean flag1 = false, flag2 = false;
        for(int i=0; i<4; i++){
            output[i] = getSlot(i).getStack();
            if(output[i] != null){
                flag1 = true;
            }
        }
        ItemStack[] input = new ItemStack[12];
        for(int i=4; i<16; i++){
            input[i-4] = getSlot(i).getStack();
            if(input[i-4] != null){
                flag2 = true;
            }
        }
        return flag1 && flag2 ? new Recipe(output, input) : null;
    }
}
