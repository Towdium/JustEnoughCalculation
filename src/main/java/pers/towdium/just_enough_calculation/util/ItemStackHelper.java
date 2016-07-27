package pers.towdium.just_enough_calculation.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.util.function.TriFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author:  Towdium
 * Created: 2016/6/14.
 */
public class ItemStackHelper {
    public static final String keyAmount = "amount";
    public static final String keyType = "type";
    public static final String keyFluid = "fluid";

    public static boolean isItemEqual(@Nullable ItemStack one, @Nullable ItemStack two) {
        return one != null && two != null && one.getItem() == two.getItem() && one.getMetadata() == two.getMetadata() &&
                NBT.equalsIgnoreJEC(one.getTagCompound(), two.getTagCompound());
    }

    @Nullable
    public static ItemStack toItemStackJEC(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;
        NBT.setData(itemStack, EnumStackAmountType.NUMBER, itemStack.stackSize);
        itemStack.stackSize = 1;
        return itemStack;
    }

    public static boolean isItemStackJEC(@Nullable ItemStack itemStack) {
        return itemStack == null || (itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey(JustEnoughCalculation.Reference.MODID));
    }

    @Nullable
    public static ItemStack mergeStack(ItemStack stack1, ItemStack stack2, boolean positive, boolean newStack, boolean mregeFluid) {
        if (!isItemEqual(stack1, stack2)) {
            return null;
        }
        ItemStack buffer = newStack ? stack1.copy() : stack1;
        BiFunction<Long, Long, Long> counter = positive ? (a, b) -> a + b : (a, b) -> a - b;
        TriFunction<ItemStack, ItemStack, EnumStackAmountType, ItemStack> merger = (a, b, type) ->
                NBT.setData(buffer, type, counter.apply(getAmountInType(type, stack1), getAmountInType(type, stack2)));
        switch (NBT.getType(stack1)) {
            case NUMBER:
                switch (NBT.getType(stack2)) {
                    case NUMBER:
                        return merger.apply(buffer, stack2, EnumStackAmountType.NUMBER);
                    case PERCENTAGE:
                        return merger.apply(buffer, stack2, EnumStackAmountType.PERCENTAGE);
                    default:
                        return null;
                }
            case PERCENTAGE:
                switch (NBT.getType(stack2)) {
                    case NUMBER:
                        return merger.apply(buffer, stack2, EnumStackAmountType.PERCENTAGE);
                    case PERCENTAGE:
                        return merger.apply(buffer, stack2, EnumStackAmountType.PERCENTAGE);
                    default:
                        return null;
                }
            case FLUID:
                switch (NBT.getType(stack2)) {
                    case FLUID:
                        if (mregeFluid && NBT.getFluid(stack1) == NBT.getFluid(stack2))
                            return merger.apply(buffer, stack2, EnumStackAmountType.FLUID);
                        else
                            return null;
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    public static long getAmountInType(EnumStackAmountType type, @Nullable ItemStack itemStack) {
        long amount = NBT.getAmount(itemStack);
        switch (NBT.getType(itemStack)) {
            case NUMBER:
                switch (type) {
                    case NUMBER:
                        return amount;
                    case PERCENTAGE:
                        return amount * 100;
                    case FLUID:
                        return amount * 1000;
                    default:
                        return 0;
                }
            case PERCENTAGE:
                switch (type) {
                    case NUMBER:
                        return (amount + 99) / 100;
                    case PERCENTAGE:
                        return amount;
                    case FLUID:
                        return amount * 10;
                    default:
                        return 0;
                }
            case FLUID:
                switch (type) {
                    case NUMBER:
                        return (amount + 999) / 1000;
                    case PERCENTAGE:
                        return (amount + 9) / 10;
                    case FLUID:
                        return amount;
                    default:
                        return 0;
                }
            default:
                return 0;
        }
    }

    @Nullable
    public static ItemStack toItemStackOfType(EnumStackAmountType type, @Nullable ItemStack itemStack) {
        return NBT.setData(itemStack, type, getAmountInType(type, itemStack));
    }

    public static ItemStack toItemStackJEC(FluidStack stack) {
        return NBT.setData(new ItemStack(JustEnoughCalculation.itemFluidContainer), stack);
    }

    public enum EnumStackAmountType {
        INVALID, NUMBER, PERCENTAGE, FLUID;

        public String getStringResult(long l) {
            switch (this) {
                case NUMBER:
                    return Utilities.cutLong(l, 5);
                case PERCENTAGE:
                    return "≈" + Utilities.cutFloat(l / 100.0f, 4);
                case FLUID:
                    return l > 1000 ? Utilities.cutFloat(l / 1000.0f, 4) + 'b' : String.valueOf(l) + "mb";
                default:
                    return "";
            }
        }

        public String getStringEditor(long l) {
            switch (this) {
                case NUMBER:
                    return Utilities.cutLong(l, 5);
                case PERCENTAGE:
                    return String.valueOf(l) + "%";
                case FLUID:
                    return l > 1000 ? Utilities.cutFloat(l / 1000.0f, 4) + 'b' : String.valueOf(l) + "mb";
                default:
                    return "";
            }
        }
    }

    public static class NBT {
        public static boolean equalsIgnoreJEC(@Nullable NBTTagCompound a, @Nullable NBTTagCompound b) {
            if (a == null && b == null) {
                return true;
            } else if (a == null || b == null) {
                return false;
            } else {
                NBTTagCompound aNew = ((NBTTagCompound) a.copy());
                aNew.removeTag(JustEnoughCalculation.Reference.MODID);
                NBTTagCompound bNew = ((NBTTagCompound) b.copy());
                bNew.removeTag(JustEnoughCalculation.Reference.MODID);
                return aNew.equals(bNew);
            }
        }

        public static ItemStack setAmount(ItemStack itemStack, long amount) {
            return setLong(itemStack, true, keyAmount, amount);
        }

        public static ItemStack setType(ItemStack itemStack, EnumStackAmountType type) {
            return setInteger(itemStack, true, keyType, type.ordinal());
        }

        public static ItemStack setFluid(ItemStack itemStack, Fluid fluid) {
            return setString(itemStack, true, keyFluid, fluid.getName());
        }

        public static ItemStack setData(ItemStack itemStack, EnumStackAmountType type, long amount) {
            setType(itemStack, type);
            return setLong(itemStack, true, keyAmount, amount);
        }

        public static ItemStack setData(ItemStack itemStack, FluidStack stack) {
            setType(itemStack, EnumStackAmountType.FLUID);
            setFluid(itemStack, stack.getFluid());
            return setAmount(itemStack, stack.amount);
        }

        public static ItemStack setInteger(ItemStack itemStack, boolean isolate, String key, int value) {
            return setValue(itemStack, isolate, (nbtTagCompound -> nbtTagCompound.setInteger(key, value)));
        }

        public static ItemStack setLong(ItemStack itemStack, boolean isolate, String key, long value) {
            return setValue(itemStack, isolate, (nbtTagCompound -> nbtTagCompound.setLong(key, value)));
        }

        public static ItemStack setBoolean(ItemStack itemStack, boolean isolate, String key, boolean value) {
            return setValue(itemStack, isolate, (nbtTagCompound -> nbtTagCompound.setBoolean(key, value)));
        }

        public static ItemStack setString(ItemStack itemStack, boolean isolate, String key, String value) {
            return setValue(itemStack, isolate, (nbtTagCompound -> nbtTagCompound.setString(key, value)));
        }

        static ItemStack setValue(ItemStack itemStack, boolean isolate, Consumer<NBTTagCompound> func) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound == null) {
                tagCompound = new NBTTagCompound();
                itemStack.setTagCompound(tagCompound);
            }
            if (isolate) {
                if (!tagCompound.hasKey(JustEnoughCalculation.Reference.MODID)) {
                    tagCompound.setTag(JustEnoughCalculation.Reference.MODID, new NBTTagCompound());
                }
                tagCompound = tagCompound.getCompoundTag(JustEnoughCalculation.Reference.MODID);
            }
            func.accept(tagCompound);
            return itemStack;
        }

        public static EnumStackAmountType getType(ItemStack itemStack) {
            return EnumStackAmountType.values()[getInteger(itemStack, true, keyType)];
        }

        public static long getAmount(ItemStack itemStack) {
            return getLong(itemStack, true, keyAmount);
        }

        public static Fluid getFluid(ItemStack itemStack) {
            return FluidRegistry.getFluid(getString(itemStack, true, keyFluid));
        }

        public static long getAmountItem(ItemStack itemStack) {
            long amount = getAmount(itemStack);
            switch (getType(itemStack)) {
                case NUMBER:
                    return amount;
                case PERCENTAGE:
                    return (amount + 99) % 100;
                case FLUID:
                    return (amount + 999) % 1000;
                default:
                    return 0;
            }
        }

        public static long getAmountInternal(ItemStack itemStack) {
            long amount = getAmount(itemStack);
            switch (getType(itemStack)) {
                case NUMBER:
                    return amount * 100;
                case PERCENTAGE:
                    return amount;
                case FLUID:
                    return amount;
                default:
                    return 0;
            }
        }

        public static int getInteger(ItemStack itemStack, boolean isolate, String key) {
            return getTag(itemStack, isolate).getInteger(key);
        }

        public static long getLong(ItemStack itemStack, boolean isolate, String key) {
            return getTag(itemStack, isolate).getLong(key);
        }

        public static String getString(ItemStack itemStack, boolean isolate, String key) {
            return getTag(itemStack, isolate).getString(key);
        }

        @SuppressWarnings("TrivialFunctionalExpressionUsage")
        static NBTTagCompound getTag(@Nonnull ItemStack itemStack, boolean isolate) {
            return isolate ? itemStack.getSubCompound(JustEnoughCalculation.Reference.MODID, true) :
                    itemStack.hasTagCompound() ? itemStack.getTagCompound() : ((Supplier<NBTTagCompound>) () -> {
                        itemStack.setTagCompound(new NBTTagCompound());
                        return itemStack.getTagCompound();
                    }).get();
        }
    }

    public static class Click {
        @Nullable
        public static ItemStack leftClick(@Nullable ItemStack itemStack) {
            return getClick(itemStack, (l -> l + 1), (l -> l + 1), (l -> 0L));
        }

        @Nullable
        public static ItemStack leftShift(@Nullable ItemStack itemStack) {
            return getClick(itemStack, (l -> l == 1 ? 10 : l + 10), (l -> l == 1 ? 10 : l + 10), (l -> 0L));
        }

        @Nullable
        public static ItemStack rightClick(@Nullable ItemStack itemStack) {
            return getClick(itemStack, (l -> l - 1), (l -> l - 1), (l -> 0L));
        }

        @Nullable
        public static ItemStack rightShift(@Nullable ItemStack itemStack) {
            return getClick(itemStack, (l -> l == 1 ? 0 : l <= 10 ? 1 : l - 10), (l -> l == 1 ? 0 : l <= 10 ? 1 : l - 10), (l -> 0L));
        }

        @Nullable
        static ItemStack getClick(@Nullable ItemStack itemStack, Function<Long, Long> funcNumber,
                                  Function<Long, Long> funcPercentage, Function<Long, Long> funcFluid) {
            if (itemStack == null) {
                return null;
            } else {
                long amount = NBT.getAmount(itemStack);
                switch (NBT.getType(itemStack)) {
                    case INVALID:
                        amount = 0;
                        break;
                    case NUMBER:
                        amount = funcNumber.apply(amount);
                        break;
                    case PERCENTAGE:
                        amount = funcPercentage.apply(amount);
                        break;
                    case FLUID:
                        amount = funcFluid.apply(amount);
                        break;
                }
                return amount == 0 ? null : NBT.setAmount(itemStack, amount);
            }
        }
    }
}
