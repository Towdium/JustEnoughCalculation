package me.towdium.jecalculation.utils;

import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Single;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Author: Towdium
 * Date:   2016/6/25.
 */
@SuppressWarnings("unused")
public class Utilities {
    final static int[] scaleTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
    static Map<String, String> dictionary = new HashMap<>();

    static {
        Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
        for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
            String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
            String modName = modEntry.getValue().getName();
            dictionary.put(lowercaseId, modName);
        }
    }

    // FLOAT FORMATTING
    public static String cutFloat(float f, int size) {
        BiFunction<Float, Integer, String> form = (fl, len) -> {
            // here 2 means floating point and unit character
            // represents for maximum acceptable decimal digits
            int scale = len - 2 - String.valueOf(fl.intValue()).length();
            // maximum decimal limits to 2
            int cut = scale > 2 ? 2 : scale;
            return String.format("%." + (cut < 0 ? 0 : cut) + 'f', fl);
        };
        int scale = (int) Math.log10(f) / 3;
        switch (scale) {
            case 0:
                return form.apply(f, size);
            case 1:
                return form.apply(f / 1000.0f, size) + 'K';
            case 2:
                return form.apply(f / 1000000.0f, size) + 'M';
            case 3:
                return form.apply(f / 1000000000.0f, size) + 'B';
            case 4:
                return form.apply(f / 1000000000000.0f, size) + 'G';
            default:
                return form.apply(f / 1000000000000000.0f, size) + 'T';
        }
    }

    public static String cutLong(long i, int size) {
        if (i < 1000) {
            return String.valueOf(i);
        } else {
            return cutFloat(i, size);
        }
    }

    // CIRCULATE STRUCTURE
    public static int circulate(int current, int total, boolean forward) {
        if (forward) {
            if (current == total - 1) return 0;
            else return current + 1;
        } else {
            if (current == 0) return total - 1;
            else return current - 1;
        }
    }

    // MOD NAME
    public static String getModName(ItemStack stack) {
        ResourceLocation tmp = stack.getItem().getRegistryName();
        if (tmp == null) return null;

        String name = tmp.toString();
        name = name.substring(0, name.indexOf(":"));
        if (name.equals("minecraft")) return "Minecraft";
        else return dictionary.get(name);
    }

    public static String getModName(FluidStack stack) {
        String name = stack.getFluid().getName();
        if (name.equals("lava") || name.equals("water")) return "Minecraft";
        else return dictionary.get(stack.getFluid().getStill().toString().split(":")[0]);
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
            current = current + 1 == total ? 0 : current + 1;
            return current;
        }

        public int prev() {
            current = current == 0 ? total - 1 : current - 1;
            return current;
        }

        public int index() {
            return current;
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
            Single<R> ret = new Single<>(null);
            if (ah == bh)
                ret.push(data.get(new Pair<>(a, b))).push(data.get(new Pair<>(b, a)));
            else
                ret.push(data.get(ah < bh ? new Pair<>(a, b) : new Pair<>(b, a)));
            return Optional.ofNullable(ret.value);
        }
    }
}
