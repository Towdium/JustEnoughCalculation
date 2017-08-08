package me.towdium.jecalculation.core;

import me.towdium.jecalculation.util.function.TriFunction;
import me.towdium.jecalculation.util.helpers.ItemStackHelper;
import me.towdium.jecalculation.util.wrappers.Pair;
import me.towdium.jecalculation.util.wrappers.Singleton;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static me.towdium.jecalculation.util.helpers.ItemStackHelper.EnumStackAmountType.INVALID;

/**
 * Author: Towdium
 * Date:   2016/6/26.
 */
public class Recipe {
    ItemStack[] output;
    ItemStack[] catalyst;
    ItemStack[] input;

    public Recipe(NBTTagCompound nbtTagCompound) {
        BiConsumer<ItemStack[], NBTTagList> writer = (itemStacks, nbtTagList) -> {
            if (itemStacks.length != nbtTagList.tagCount())
                throw new IllegalArgumentException("Length not match");
            for (int i = 0; i < itemStacks.length; i++) {
                itemStacks[i] = ItemStackHelper.readFromNBT(nbtTagList.getCompoundTagAt(i));
            }
        };
        output = new ItemStack[4];
        input = new ItemStack[12];
        catalyst = new ItemStack[4];
        writer.accept(output, nbtTagCompound.getTagList("output", 10));
        writer.accept(catalyst, nbtTagCompound.getTagList("catalyst", 10));
        writer.accept(input, nbtTagCompound.getTagList("input", 10));
    }

    public Recipe(ItemStack[] output, ItemStack[] catalyst, ItemStack[] input) {
        TriFunction<ItemStack[], Integer, String, String> checkArray = (itemStackIn, amount, type) -> {
            if (itemStackIn.length != amount)
                return "Inspection for " + type + " failed: " + "length not match: expected - " + amount + ", actual - " + itemStackIn.length;
            for (ItemStack itemStack : itemStackIn) {
                if (!ItemStackHelper.isItemStackJEC(itemStack))
                    return "Inspection for " + type + " failed: " + "itemStack illegal: " + itemStack;
            }
            return null;
        };

        Function<ItemStack[], ItemStack[]> copyArray = itemStacks -> {
            ItemStack[] buffer = new ItemStack[itemStacks.length];
            int count = -1;
            for (ItemStack itemStack : itemStacks) {
                buffer[++count] = itemStack.copy();
            }
            return buffer;
        };
        Singleton<String> result = new Singleton<>(null);
        result.push(checkArray.apply(output, 4, "output"));
        result.push(checkArray.apply(catalyst, 4, "catalyst"));
        result.push(checkArray.apply(input, 12, "input"));
        if (result.value != null)
            throw new IllegalArgumentException(result.value);
        this.output = copyArray.apply(output);
        this.catalyst = copyArray.apply(catalyst);
        this.input = copyArray.apply(input);
    }

    public long getAmountOutputInternal(ItemStack itemStack) {
        int index = getIndexOutput(itemStack);
        return index == -1 ? 0 : ItemStackHelper.NBT.getAmountInternal(output[index]);
    }

    public Pair<Long, ItemStackHelper.EnumStackAmountType> getAmountOutput(ItemStack itemStack) {
        int index = getIndexOutput(itemStack);
        return index == -1 ? new Pair<>(0L, INVALID) :
                new Pair<>(ItemStackHelper.NBT.getAmount(output[index]),
                        ItemStackHelper.NBT.getType(output[index]));
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

    public NBTTagCompound writeToNbt() {
        Function<ItemStack[], NBTTagList> former = itemStacks -> {
            NBTTagList ret = new NBTTagList();
            for (ItemStack stack : itemStacks) {
                ret.appendTag(ItemStackHelper.writeToNBT(stack));
            }
            return ret;
        };
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag("output", former.apply(output));
        ret.setTag("catalyst", former.apply(catalyst));
        ret.setTag("input", former.apply(input));
        return ret;
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
}
