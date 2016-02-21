package pers.towdium.tudicraft.core;

import net.minecraft.item.ItemStack;

/**
 * @author Towdium
 */
public class Recipe {
    ItemStack[] output;
    ItemStack[] input;

    public Recipe(ItemStack[] output, ItemStack[] input) {
        if(output.length != 4 || input.length != 12){
            throw new IllegalArgumentException("Incorrect array size");
        }
        this.output = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            ItemStack itemstack = output[i];
            if(itemstack != null){
                boolean flag = false;
                for (int j = 0; j < i; j++) {
                    if (this.output[j] != null && ItemStackWrapper.isTypeEqual(this.output[j], itemstack)) {
                        this.output[j].stackSize += itemstack.stackSize;
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    this.output[i] = itemstack.copy();
                }
            }
        }
        this.input = new ItemStack[12];
        for (ItemStack itemstack : input) {
            if(itemstack != null){
                for(int i=0; i<12; i++){
                    if(ItemStackWrapper.isTypeEqual(this.input[i], itemstack)){
                        this.input[i].stackSize += itemstack.stackSize;
                        break;
                    } else if (this.input[i] == null){
                        this.input[i] = itemstack.copy();
                        break;
                    }
                }
            }
        }
    }

    public  ItemStack getOutput(ItemStack itemStack){
        for(int i = 0; i<4; i++){
            if(ItemStackWrapper.isTypeEqual(output[i], itemStack)){
                return output[i];
            }
        }
        return null;
    }

    public int getOutputIndex(ItemStack itemStack){
        for(int i = 0; i<4; i++){
            if(ItemStackWrapper.isTypeEqual(output[i], itemStack)){
                return i;
            }
        }
        return -1;
    }

    public int getOutputAmount(ItemStack itemStack){
        for(int i = 0; i<4; i++){
            if(ItemStackWrapper.isTypeEqual(output[i], itemStack)){
                return ItemStackWrapper.getUnifiedAmount(output[i]);
            }
        }
        return 0;
    }

    public int getInputAmount(ItemStack itemStack){
        for(int i = 0; i<12; i++){
            if(ItemStackWrapper.isTypeEqual(input[i], itemStack)){
                return ItemStackWrapper.getUnifiedAmount(input[i]);
            }
        }
        return 0;
    }

    public boolean getHasOutput(ItemStack itemStack){
        return getOutputIndex(itemStack) != -1;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Recipe){
            for(int i=0; i<4; i++){
                if(!ItemStackWrapper.isStackEqual(output[i], ((Recipe)obj).output[i])){
                    return false;
                }
            }
            for(int i=0; i<12; i++){
                if(!ItemStackWrapper.isStackEqual(input[i], ((Recipe)obj).input[i])){
                    return false;
                }
            }
            return true;
        }else {
            return false;
        }
    }

    public boolean isOutputEqual(Recipe recipe){
        for(int i=0; i<4; i++){
            if(!ItemStackWrapper.isStackEqual(output[i], recipe.output[i])){
                return false;
            }
        }
        return true;
    }
}
