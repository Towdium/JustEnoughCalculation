package pers.towdium.just_enough_calculation.core;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.function.TriConsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: Towdium
 * Date:   2016/6/26.
 */
public class Recipe {
    ItemStack[] output;
    ItemStack[] catalyst;
    ItemStack[] input;

    public Recipe(ItemStack[] output, ItemStack[] catalyst, ItemStack[] input) {
        BiFunction<ItemStack[], Integer, Boolean> checkArray = (itemStackIn, amount) -> {
            if (itemStackIn.length != amount)
                return false;
            for (ItemStack itemStack : itemStackIn) {
                if (!ItemStackHelper.isItemStackJEC(itemStack))
                    return false;
            }
            return true;
        };
        Function<ItemStack[], ItemStack[]> copyArray = itemStacks -> {
            ItemStack[] buffer = new ItemStack[itemStacks.length];
            int count = -1;
            for (ItemStack itemStack : itemStacks) {
                buffer[++count] = itemStack == null ? null : itemStack.copy();
            }
            return buffer;
        };
        if (!checkArray.apply(output, 4) || !checkArray.apply(catalyst, 4) || !checkArray.apply(input, 12)) {
            throw new IllegalArgumentException();
        }
        this.output = copyArray.apply(output);
        this.catalyst = copyArray.apply(catalyst);
        this.input = copyArray.apply(input);
    }

    public int getIndexInput(ItemStack itemStack) {
        return getIndex(input, itemStack);
    }

    public int getIndexCatalyst(ItemStack itemStack) {
        return getIndex(catalyst, itemStack);
    }

    public int getIndexOutput(ItemStack itemStack) {
        return getIndex(output, itemStack);
    }

    int getIndex(ItemStack[] itemStacks, ItemStack itemStackIn) {
        for (int i = 0; i < itemStacks.length; i++) {
            if (ItemStackHelper.isItemEqual(itemStacks[i], itemStackIn)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Recipe)) {
            return false;
        }
        BiFunction<ItemStack[], ItemStack[], Boolean> checkEqual = (itemStacks1, itemStacks2) -> {
            if (itemStacks1.length != itemStacks2.length)
                return false;
            int len = itemStacks1.length;
            for (int i = 0; i < len; i++) {
                if (!ItemStack.areItemStacksEqual(itemStacks1[i], itemStacks2[i])) {
                    return false;
                }
            }
            return true;
        };
        Recipe r = (Recipe) obj;
        return checkEqual.apply(output, r.output) && checkEqual.apply(catalyst, r.catalyst) && checkEqual.apply(input, r.input);
    }

    public List<ItemStack> getOutput() {
        return getList(output);
    }

    public List<ItemStack> getCatalyst() {
        return getList(catalyst);
    }

    public List<ItemStack> getInput() {
        return getList(input);
    }

    public List<ItemStack> getList(ItemStack[] itemStacks) {
        List<ItemStack> buffer = new ArrayList<>();
        Collections.addAll(buffer, itemStacks);
        return buffer;
    }

    public enum EnumStackIOType {
        OUTPUT, CATALYST, INPUT;

        public int getLength() {
            switch (this) {
                case OUTPUT:
                    return 4;
                case CATALYST:
                    return 4;
                case INPUT:
                    return 12;
                default:
                    return 0;
            }
        }
    }

    public static class IOUtl {
        public static Recipe fromByte(ByteBuf buf) {
            return fromContainer((enumStackIOType, integer) -> ByteBufUtils.readItemStack(buf), () -> ByteBufUtils.readUTF8String(buf));
        }

        public static Recipe fromNbt(NBTTagCompound tagCompound) {
            return fromContainer((enumStackIOType, integer) -> {
                NBTTagCompound temp = tagCompound.getCompoundTag(String.valueOf(enumStackIOType.ordinal()));
                temp = temp.getCompoundTag(String.valueOf(integer));
                return ItemStack.loadItemStackFromNBT(temp);
            }, () -> tagCompound.getString("Group"));
        }

        public static void toByte(ByteBuf buf, Recipe recipe) {
            toContainer((enumStackIOType, integer, itemStack) -> ByteBufUtils.writeItemStack(buf, itemStack), recipe);
        }

        public static void toNbt(Recipe recipe) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            toContainer((enumStackIOType, integer, itemStack) -> {
                NBTTagCompound stackInfo = new NBTTagCompound();
                itemStack.writeToNBT(stackInfo);
                String temp = String.valueOf(enumStackIOType.ordinal());
                if (integer == 0) {
                    tagCompound.setTag(temp, new NBTTagCompound());
                }
                tagCompound.getCompoundTag(temp).setTag(String.valueOf(integer), stackInfo);
            }, recipe);
        }

        static Recipe fromContainer(BiFunction<EnumStackIOType, Integer, ItemStack> funcStack, Supplier<String> funcGroup) {
            Function<EnumStackIOType, ItemStack[]> readArray = (type) -> {
                int len = type.getLength();
                ItemStack[] buffer = new ItemStack[len];
                for (int i = 0; i < len; i++) {
                    buffer[i] = funcStack.apply(type, i);
                }
                return buffer;
            };
            return new Recipe(readArray.apply(EnumStackIOType.OUTPUT), readArray.apply(EnumStackIOType.CATALYST),
                    readArray.apply(EnumStackIOType.INPUT));
        }

        static void toContainer(TriConsumer<EnumStackIOType, Integer, ItemStack> funcStack, Recipe recipe) {
            BiConsumer<EnumStackIOType, ItemStack[]> writeArray = ((type, itemStacks) -> {
                int len = type.getLength();
                for (int i = 0; i < len; i++) {
                    funcStack.accept(type, i, itemStacks[i]);
                }
            });
            writeArray.accept(EnumStackIOType.OUTPUT, recipe.output);
            writeArray.accept(EnumStackIOType.CATALYST, recipe.catalyst);
            writeArray.accept(EnumStackIOType.INPUT, recipe.input);
        }
    }
}
