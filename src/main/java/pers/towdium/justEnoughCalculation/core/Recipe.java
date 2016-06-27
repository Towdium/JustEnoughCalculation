package pers.towdium.justEnoughCalculation.core;


import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import pers.towdium.justEnoughCalculation.util.ItemStackHelper;
import pers.towdium.justEnoughCalculation.util.function.TriConsumer;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Author: Towdium
 * Date:   2016/6/26.
 */
public class Recipe {
    ItemStack[] output;
    ItemStack[] catalyst;
    ItemStack[] input;

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

    public Recipe(ItemStack[] output, ItemStack[] catalyst, ItemStack[] input) {
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
    }

    @Nullable
    public ItemStack getItemStackInput(ItemStack itemStack){
        return getItemSack(input, itemStack);
    }

    @Nullable
    public ItemStack getItemStackCatalyst(ItemStack itemStack){
        return getItemSack(catalyst, itemStack);
    }

    @Nullable
    public ItemStack getItemStackOutput(ItemStack itemStack){
        return getItemSack(output, itemStack);
    }

    @Nullable
    ItemStack getItemSack(ItemStack[] itemStacks, ItemStack itemStackIn){
        for(ItemStack itemStack : itemStacks){
            if(ItemStackHelper.isTypeEqual(itemStack, itemStackIn)){
                return itemStack;
            }
        }
        return null;
    }

    public static class IOUtl{
        public static Recipe fromByte(ByteBuf buf){
            return fromContainer((enumStackIOType, integer) -> ByteBufUtils.readItemStack(buf));
        }

        public static Recipe fromNbt(NBTTagCompound tagCompound){
            return fromContainer((enumStackIOType, integer) -> {
                NBTTagCompound temp = tagCompound.getCompoundTag(String.valueOf(enumStackIOType.ordinal()));
                temp = temp.getCompoundTag(String.valueOf(integer));
                return ItemStack.loadItemStackFromNBT(temp);
            });
        }

        public static void toByte(ByteBuf buf, Recipe recipe) {
            toContainer((enumStackIOType, integer, itemStack) -> ByteBufUtils.writeItemStack(buf, itemStack), recipe);
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
            }, recipe);
        }

        static Recipe fromContainer(BiFunction<EnumStackIOType, Integer, ItemStack> func){
            Function<EnumStackIOType, ItemStack[]> readArray = (type) -> {
                int len = type.getLength();
                ItemStack[] buffer = new ItemStack[len];
                for(int i = 0; i <len; i++){
                    buffer[i] = func.apply(type, i);
                }
                return buffer;
            };
            return new Recipe(readArray.apply(EnumStackIOType.OUTPUT),
                    readArray.apply(EnumStackIOType.CATALYST), readArray.apply(EnumStackIOType.INPUT));
        }

        static void toContainer(TriConsumer<EnumStackIOType, Integer, ItemStack> func, Recipe recipe) {
            BiConsumer<EnumStackIOType, ItemStack[]> writeArray = ((type, itemStacks) -> {
                int len = type.getLength();
                for(int i = 0; i <len; i++){
                    func.accept(type, i, itemStacks[i]);
                }
            });
            writeArray.accept(EnumStackIOType.OUTPUT, recipe.output);
            writeArray.accept(EnumStackIOType.CATALYST, recipe.catalyst);
            writeArray.accept(EnumStackIOType.INPUT, recipe.input);
        }
    }
}
