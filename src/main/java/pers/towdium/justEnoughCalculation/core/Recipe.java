package pers.towdium.justEnoughCalculation.core;


import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import pers.towdium.justEnoughCalculation.util.ItemStackHelper;
import pers.towdium.justEnoughCalculation.util.function.TriConsumer;

import java.util.function.*;

/**
 * Author: Towdium
 * Date:   2016/6/26.
 */
public class Recipe {
    ItemStack[] output;
    ItemStack[] catalyst;
    ItemStack[] input;
    String group;

    enum EnumStackIOType{
        OUTPUT, CATALYST, INPUT;

        public int getLength(){
            switch (this){
                case OUTPUT: return 4;
                case CATALYST: return 4;
                case INPUT: return 12;
                default: return 0;
            }
        }
    }

    public Recipe(ItemStack[] output, ItemStack[] catalyst, ItemStack[] input, String group) {
        BiFunction<ItemStack[], Integer, Boolean> checkArray = (itemStackIn, amount) -> {
            if(itemStackIn.length != amount)
                return false;
            for(ItemStack itemStack : itemStackIn){
                if (!ItemStackHelper.isItemStackJEC(itemStack))
                    return false;
            }
            return true;
        };
        Function<ItemStack[], ItemStack[]> copyArray = itemStacks -> {
            ItemStack[] buffer = new ItemStack[itemStacks.length];
            int count = -1;
            for(ItemStack itemStack : itemStacks){
                buffer[++count] = itemStack.copy();
            }
            return buffer;
        };
        if(!checkArray.apply(output, 4) || !checkArray.apply(catalyst, 4) || !checkArray.apply(input, 12)){
            throw new IllegalArgumentException();
        }
        this.output = copyArray.apply(output);
        this.catalyst = copyArray.apply(catalyst);
        this.input = copyArray.apply(input);
        this.group = group;
    }

    public int getIndexInput(ItemStack itemStack){
        return getIndex(input, itemStack);
    }

    public int getIndexCatalyst(ItemStack itemStack){
        return getIndex(catalyst, itemStack);
    }

    public int getIndexOutput(ItemStack itemStack){
        return getIndex(output, itemStack);
    }

    int getIndex(ItemStack[] itemStacks, ItemStack itemStackIn){
        for(int i = 0; i < itemStacks.length; i++) {
            if(ItemStackHelper.isItemEqual(itemStacks[i], itemStackIn)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Recipe)){
            return false;
        }
        BiFunction<ItemStack[], ItemStack[], Boolean> checkEqual = (itemStacks1, itemStacks2) -> {
            if(itemStacks1.length != itemStacks2.length)
                return false;
            int len = itemStacks1.length;
            for(int i = 0; i < len; i++) {
                if(!ItemStack.areItemStacksEqual(itemStacks1[i], itemStacks2[i])){
                    return false;
                }
            }
            return true;
        };
        Recipe r = (Recipe) obj;
        return checkEqual.apply(output, r.output) && checkEqual.apply(catalyst, r.catalyst) &&
                checkEqual.apply(input, r.input) && group.equals(r.group);
    }

    public static class IOUtl{
        public static Recipe fromByte(ByteBuf buf){
            return fromContainer((enumStackIOType, integer) -> ByteBufUtils.readItemStack(buf), () -> ByteBufUtils.readUTF8String(buf));
        }

        public static Recipe fromNbt(NBTTagCompound tagCompound){
            return fromContainer((enumStackIOType, integer) -> {
                NBTTagCompound temp = tagCompound.getCompoundTag(String.valueOf(enumStackIOType.ordinal()));
                temp = temp.getCompoundTag(String.valueOf(integer));
                return ItemStack.loadItemStackFromNBT(temp);
            }, () -> tagCompound.getString("Group"));
        }

        public static void toByte(ByteBuf buf, Recipe recipe) {
            toContainer((enumStackIOType, integer, itemStack) -> ByteBufUtils.writeItemStack(buf, itemStack), (string) -> ByteBufUtils.writeUTF8String(buf, string), recipe);
        }

        public static void toNbt(Recipe recipe){
            NBTTagCompound tagCompound = new NBTTagCompound();
            toContainer((enumStackIOType, integer, itemStack) -> {
                NBTTagCompound stackInfo = new NBTTagCompound();
                itemStack.writeToNBT(stackInfo);
                String temp = String.valueOf(enumStackIOType.ordinal());
                if(integer == 0){
                    tagCompound.setTag(temp, new NBTTagCompound());
                }
                tagCompound.getCompoundTag(temp).setTag(String.valueOf(integer), stackInfo);
            }, (s -> tagCompound.setString("Group", s)) , recipe);
        }

        static Recipe fromContainer(BiFunction<EnumStackIOType, Integer, ItemStack> funcStack, Supplier<String> funcGroup){
            Function<EnumStackIOType, ItemStack[]> readArray = (type) -> {
                int len = type.getLength();
                ItemStack[] buffer = new ItemStack[len];
                for(int i = 0; i <len; i++){
                    buffer[i] = funcStack.apply(type, i);
                }
                return buffer;
            };
            return new Recipe(readArray.apply(EnumStackIOType.OUTPUT), readArray.apply(EnumStackIOType.CATALYST),
                    readArray.apply(EnumStackIOType.INPUT), funcGroup.get());
        }

        static void toContainer(TriConsumer<EnumStackIOType, Integer, ItemStack> funcStack, Consumer<String> funcGroup, Recipe recipe) {
            BiConsumer<EnumStackIOType, ItemStack[]> writeArray = ((type, itemStacks) -> {
                int len = type.getLength();
                for(int i = 0; i <len; i++){
                    funcStack.accept(type, i, itemStacks[i]);
                }
            });
            writeArray.accept(EnumStackIOType.OUTPUT, recipe.output);
            writeArray.accept(EnumStackIOType.CATALYST, recipe.catalyst);
            writeArray.accept(EnumStackIOType.INPUT, recipe.input);
            funcGroup.accept(recipe.group);
        }
    }
}
