package pers.towdium.justEnoughCalculation.core;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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

    public ImmutableList<ItemStack> getOutput(){
        ImmutableList.Builder<ItemStack> builder = new ImmutableList.Builder<>();
        for(ItemStack itemStack : output){
            if(itemStack != null){
                builder.add(itemStack.copy());
            }
        }
        return builder.build();
    }

    public ImmutableList<ItemStack> getInput(){
        ImmutableList.Builder<ItemStack> builder = new ImmutableList.Builder<>();
        for(ItemStack itemStack : input){
            if(itemStack != null){
                builder.add(itemStack.copy());
            }
        }
        return builder.build();
    }

    public static class ByteBufUtl{
        public static Recipe fromByte(ByteBuf buf){
            short a = buf.readShort();
            if(a == 0){
                return null;
            }
            short b = buf.readShort();
            ItemStack[] output = new ItemStack[4];
            ItemStack[] input = new ItemStack[12];
            for(int i=0; i<a; i++){
                output[i] = ByteBufUtils.readItemStack(buf);
            }
            for(int i=0; i<b; i++){
                input[i] = ByteBufUtils.readItemStack(buf);
            }
            return new Recipe(output, input);
        }

        public static void toByte(ByteBuf buf, Recipe recipe){
            if(recipe == null){
                buf.writeShort(0);
                return;
            }
            short a = 0;
            short b = 0;
            for(ItemStack itemStack : recipe.output){
                if(itemStack != null){
                    a++;
                }
            }
            for(ItemStack itemStack : recipe.input){
                if(itemStack != null){
                    b++;
                }
            }
            buf.writeShort(a);
            buf.writeShort(b);
            ItemStack[] output1 = recipe.output;
            for (ItemStack itemStack : output1) {
                if(itemStack != null){
                    ByteBufUtils.writeItemStack(buf, itemStack);
                }
            }
            ItemStack[] input1 = recipe.input;
            for (ItemStack itemStack : input1) {
                if(itemStack != null){
                    ByteBufUtils.writeItemStack(buf, itemStack);
                }
            }
        }
    }

    public static class NBTUtl{
        public static NBTTagCompound toNBT(Recipe recipe){
            NBTTagCompound tagCompound = new NBTTagCompound();
            for(int i=0; i<4; i++){
                if(recipe.output[i] != null){
                    NBTTagCompound buffer = new NBTTagCompound();
                    recipe.output[i].writeToNBT(buffer);
                    tagCompound.setTag(i+"", buffer);
                }
            }
            for(int i=0; i<12; i++){
                if(recipe.input[i] != null){
                    NBTTagCompound buffer = new NBTTagCompound();
                    recipe.input[i].writeToNBT(buffer);
                    tagCompound.setTag((i+4)+"", buffer);
                }
            }
            return tagCompound;
        }

        public static Recipe fromNBT (NBTTagCompound tagCompound){
            ItemStack[] output = new ItemStack[4];
            ItemStack[] input = new ItemStack[12];
            for(int i=0; i<4; i++){
                NBTTagCompound buffer = tagCompound.getCompoundTag(String.valueOf(i));
                if(buffer != null){
                    output[i] = ItemStack.loadItemStackFromNBT(buffer);
                }
            }
            for(int i=0; i<12; i++){
                NBTTagCompound buffer = tagCompound.getCompoundTag(String.valueOf(i+4));
                if(buffer != null){
                    input[i] = ItemStack.loadItemStackFromNBT(buffer);
                }
            }
            return new Recipe(output, input);
        }
    }
}
