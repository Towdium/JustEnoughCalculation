package me.towdium.jecalculation.utils;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.polyfill.Polyfill;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemStackHelper {
    public static final String keyAmount = "amount";
    public static final String keyType = "type";

    public enum EnumStackAmountType {
        INVALID, NUMBER, PERCENTAGE, FLUID;

        public String getDisplayString(long l) {
            switch (this) {
                case NUMBER:
                    if (l < 999) {
                        return String.valueOf(l);
                    } else if (l < 9999) {
                        return String.format("%.2fK", (double) l / 1000);
                    } else if (l < 99999) {
                        return String.format("%.1fK", (double) l / 1000);
                    } else if (l < 999999) {
                        return String.format("%.0fK", (double) l / 1000);
                    } else if (l < 9999999) {
                        return String.format("%.2fM", (double) l / 1000000);
                    } else if (l < 99999999) {
                        return String.format("%.1fM", (double) l / 1000000);
                    } else if (l < 999999999) {
                        return String.format("%.0fB", (double) l / 1000000);
                    } else if (l < 9999999999L) {
                        return String.format("%.2fB", (double) l / 1000000000);
                    } else if (l < 99999999999L) {
                        return String.format("%.1fB", (double) l / 1000000000);
                    } else if (l < 999999999999L) {
                        return String.format("%.0fT", (double) l / 1000000000);
                    } else if (l < 9999999999999L) {
                        return String.format("%.2fT", (double) l / 1000000000000L);
                    } else if (l < 99999999999999L) {
                        return String.format("%.1fT", (double) l / 1000000000000L);
                    } else if (l < 999999999999999L) {
                        return String.format("%.0fG", (double) l / 1000000000000L);
                    } else if (l < 9999999999999999L) {
                        return String.format("%.2fG", (double) l / 1000000000000000L);
                    } else if (l < 99999999999990999L) {
                        return String.format("%.1fG", (double) l / 1000000000000000L);
                    } else {
                        return String.format("%.0fG", (double) l / 1000000000000000L);
                    }
                case PERCENTAGE:
                    if (l < 999) {
                        return String.valueOf(l);
                    } else if (l < 9999) {
                        return String.format("≈%.1fK", (double) l / 1000);
                    } else if (l < 99999) {
                        return String.format("≈%.0fK", (double) l / 1000);
                    } else if (l < 999999) {
                        return String.format("≈%.1fM", (double) l / 1000000);
                    } else if (l < 9999999) {
                        return String.format("≈%.1fM", (double) l / 1000000);
                    } else if (l < 99999999) {
                        return String.format("≈%.0fM", (double) l / 1000000);
                    } else if (l < 999999999) {
                        return String.format("≈%.1fB", (double) l / 1000000000);
                    } else if (l < 9999999999L) {
                        return String.format("≈%.1fB", (double) l / 1000000000);
                    } else if (l < 99999999999L) {
                        return String.format("≈%.0fB", (double) l / 1000000000);
                    } else if (l < 999999999999L) {
                        return String.format("≈%.1fT", (double) l / 1000000000000L);
                    } else if (l < 9999999999999L) {
                        return String.format("≈%.1fT", (double) l / 1000000000000L);
                    } else if (l < 99999999999999L) {
                        return String.format("≈%.0fT", (double) l / 1000000000000L);
                    } else if (l < 999999999999999L) {
                        return String.format("≈%.1fG", (double) l / 1000000000000000L);
                    } else if (l < 9999999999999999L) {
                        return String.format("≈%.1fG", (double) l / 1000000000000000L);
                    } else {
                        return String.format("≈%.0fG", (double) l / 1000000000000000L);
                    }
                case FLUID:
                    if (l < 999) {
                        return l + "mb";
                    } else if (l < 9999) {
                        return String.format("%.2fb", (double) l / 1000);
                    } else if (l < 99999) {
                        return String.format("%.1fb", (double) l / 1000);
                    } else if (l < 999999) {
                        return String.format("%.0fb", (double) l / 1000);
                    } else if (l < 9999999) {
                        return String.format("%.1fKb", (double) l / 1000000);
                    } else if (l < 99999999) {
                        return String.format("%.0fKb", (double) l / 1000000);
                    } else if (l < 999999999) {
                        return String.format("%.1fMb", (double) l / 1000000000);
                    } else if (l < 9999999999L) {
                        return String.format("%.1fMb", (double) l / 1000000000);
                    } else if (l < 99999999999L) {
                        return String.format("%.0fMb", (double) l / 1000000000);
                    } else if (l < 999999999999L) {
                        return String.format("%.1fBb", (double) l / 1000000000000L);
                    } else if (l < 9999999999999L) {
                        return String.format("%.1fBb", (double) l / 1000000000000L);
                    } else if (l < 99999999999999L) {
                        return String.format("%.0fBb", (double) l / 1000000000000L);
                    } else if (l < 999999999999999L) {
                        return String.format("%.1fTb", (double) l / 1000000000000000L);
                    } else if (l < 9999999999999999L) {
                        return String.format("%.1fTb", (double) l / 1000000000000000L);
                    } else {
                        return String.format("%.0fTb", (double) l / 1000000000000000L);
                    }
                default:
                    return "N/A";
            }
        }
    }


    public static boolean isTypeEqual(@Nullable ItemStack one, @Nullable ItemStack two) {
        return one != null && two != null && one.getItem() == two.getItem() && getMetadata(one) == getMetadata(two) &&
               NBT.equalsIgnoreJEC(one.getTagCompound(), two.getTagCompound());
    }


    @Nullable
    public static ItemStack toItemStackJEC(ItemStack itemStack) {
        if (itemStack == null)
            return null;
        NBT.setData(itemStack, EnumStackAmountType.NUMBER, itemStack.stackSize);
        itemStack.stackSize = 1;
        return itemStack;
    }

    public static boolean isItemStackJEC(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.getTagCompound() != null &&
               itemStack.getTagCompound().hasKey(JustEnoughCalculation.Reference.MODID);
    }

    private static int getMetadata(ItemStack stack) {
        return stack.getItemDamage();
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

        public static ItemStack setData(ItemStack itemStack, EnumStackAmountType type, long amount) {
            setInteger(itemStack, true, keyType, type.ordinal());
            return setLong(itemStack, true, keyAmount, amount);
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

        static NBTTagCompound getTag(@Nonnull ItemStack itemStack, boolean isolate) {
            if (isolate) {
                return Polyfill.getSubCompound(itemStack, JustEnoughCalculation.Reference.MODID, true);
            } else {
                if (!itemStack.hasTagCompound()) {
                    itemStack.setTagCompound(new NBTTagCompound());
                }
                return itemStack.getTagCompound();
            }
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
            return getClick(itemStack, (l -> l == 1 ? 0 : l <= 10 ? 1 : l - 10),
                            (l -> l == 1 ? 0 : l <= 10 ? 1 : l - 10), (l -> 0L));
        }

        @Nullable
        static ItemStack getClick(@Nullable ItemStack itemStack,
                                  Function<Long, Long> funcNumber,
                                  Function<Long, Long> funcPercentage,
                                  Function<Long, Long> funcFluid) {
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
