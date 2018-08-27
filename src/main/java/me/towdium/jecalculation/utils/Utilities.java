package me.towdium.jecalculation.utils;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.capacity.JecaCapability;
import me.towdium.jecalculation.data.structure.Recipes;
import me.towdium.jecalculation.item.ItemCalculator;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Author: Towdium
 * Date:   2016/6/25.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Utilities {
    final static int[] scaleTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};

    public static boolean stackEqual(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage() &&
                ((a.getTagCompound() == null && b.getTagCompound() == null) ||
                        (a.getTagCompound() != null && a.getTagCompound().equals(b.getTagCompound())));
    }

    // FLOAT FORMATTING
    public static String cutNumber(float f, int size) {
        BiFunction<Float, Integer, String> form = (fl, len) -> {
            String ret = Float.toString(fl);
            if (ret.endsWith(".0")) ret = ret.substring(0, ret.length() - 2);
            if (ret.length() > len) ret = ret.substring(0, len);
            return ret;
        };
        int scale = (int) Math.log10(f) / 3;
        switch (scale) {
            case 0:
                return form.apply(f, size);
            case 1:
                return form.apply(f / 1000.0f, size - 1) + 'K';
            case 2:
                return form.apply(f / 1000000.0f, size - 1) + 'M';
            case 3:
                return form.apply(f / 1000000000.0f, size - 1) + 'B';
            case 4:
                return form.apply(f / 1000000000000.0f, size - 1) + 'G';
            default:
                return form.apply(f / 1000000000000000.0f, size - 1) + 'T';
        }
    }

    // MOD NAME
    @Nullable
    public static String getModName(Item item) {
        ResourceLocation tmp = item.getRegistryName();
        if (tmp == null) return null;
        String id = tmp.getResourceDomain();
        return id.equals("minecraft") ? "Minecraft" : Loader.instance().getIndexedModList().get(id).getName();
    }

    public static String getModName(Fluid fluid) {
        String name = fluid.getName();
        if (name.equals("lava") || name.equals("water")) return "Minecraft";
        else return Loader.instance().getIndexedModList().get(fluid.getStill().getResourceDomain()).getName();
    }

    public static NBTTagCompound getTag(ItemStack is) {
        return is.getOrCreateSubCompound(JustEnoughCalculation.Reference.MODID);
    }

    public static boolean contains(String s1, String s2) {
        return s1.contains(s2);
    }

    // get calculator item in player inventory
    public static Optional<ItemStack> getStack() {
        InventoryPlayer inv = Minecraft.getMinecraft().player.inventory;
        ItemStack is = inv.getCurrentItem();
        if (is.getItem() instanceof ItemCalculator) return Optional.of(is);
        is = inv.offHandInventory.get(0);
        return Optional.ofNullable(is.getItem() instanceof ItemCalculator ? is : null);
    }

    public static Recipes getRecipes(EntityPlayer player) {
        //noinspection ConstantConditions
        return player.getCapability(JecaCapability.CAPABILITY_RECORD, EnumFacing.UP);
    }

    public static void setStack(ItemStack is) {

    }

    public static class Timer {
        long time = System.currentTimeMillis();
        boolean running = false;

        public void setState(boolean b) {
            if (!b && running) running = false;
            if (b && !running) {
                running = true;
                time = System.currentTimeMillis();
            }
        }

        public long getTime() {
            return running ? System.currentTimeMillis() - time : 0;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Circulator {
        int total, current;

        public Circulator(int total) {
            this(total, 0);
        }

        public Circulator(int total, int current) {
            this.total = total;
            this.current = current;
        }

        public int next() {
            return current + 1 == total ? 0 : current + 1;
        }

        public int prev() {
            return current == 0 ? total - 1 : current - 1;
        }

        public Circulator move(int steps) {
            current += steps;
            if (current < 0) current += (-current) / total * total + total;
            else current = current % total;
            return this;
        }

        public int current() {
            return current;
        }

        public Circulator set(int index) {
            if (index >= 0 && index < total) current = index;
            else throw new RuntimeException(String.format("Expected: [0, %d), given: %d.", total, index));
            return this;
        }

        public Circulator copy() {
            return new Circulator(total).set(current);
        }
    }

    public static class ReversedIterator<T> implements Iterator<T> {
        ListIterator<T> i;

        public ReversedIterator(List<T> l) {
            i = l.listIterator(l.size());
        }

        public ReversedIterator(ListIterator<T> i) {
            while (i.hasNext()) i.next();
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return i.hasPrevious();
        }

        @Override
        public T next() {
            return i.previous();
        }

        public Stream<T> stream() {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
        }
    }

    public static class Relation<T, R> {
        HashMap<Pair<T, T>, R> data = new HashMap<>();

        public void add(T a, T b, R relation) {
            data.put(a.hashCode() < b.hashCode() ? new Pair<>(a, b) : new Pair<>(b, a), relation);
        }

        public Optional<R> get(T a, T b) {
            int ah = a.hashCode();
            int bh = b.hashCode();
            Wrapper<R> ret = new Wrapper<>(null);
            if (ah == bh)
                ret.push(data.get(new Pair<>(a, b))).push(data.get(new Pair<>(b, a)));
            else
                ret.push(data.get(ah < bh ? new Pair<>(a, b) : new Pair<>(b, a)));
            return Optional.ofNullable(ret.value);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class I18n {
        public static Pair<String, Boolean> search(String translateKey, Object... parameters) {
            Pair<String, Boolean> ret = new Pair<>(null, null);
            String buffer = net.minecraft.client.resources.I18n.format(translateKey, parameters);
            ret.two = !buffer.equals(translateKey);
            buffer = StringEscapeUtils.unescapeJava(buffer);
            ret.one = buffer.replace("\t", "    ");
            return ret;
        }

        public static String format(String translateKey, Object... parameters) {
            return search(translateKey, parameters).one;
        }
    }

    public static class Recent<T> {
        LinkedList<T> data = new LinkedList<>();
        BiPredicate<T, T> tester;
        int limit;

        public Recent(BiPredicate<T, T> tester, int limit) {
            this.tester = tester;
            this.limit = limit;
        }

        public Recent(int limit) {
            this.limit = limit;
        }

        public void push(T obj) {
            data.removeIf(t -> tester != null ? tester.test(t, obj) : t.equals(obj));
            data.push(obj);
            if (data.size() > limit) data.pop();
        }

        public List<T> toList() {
            //noinspection unchecked
            return (List<T>) data.clone();
        }
    }
}
