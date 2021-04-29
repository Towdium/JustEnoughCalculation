package me.towdium.jecalculation.utils;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Single;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Author: Towdium
 * Date:   2016/6/25.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
@ParametersAreNonnullByDefault
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
    public static String cutNumber(float f, int size) {
        BiFunction<Float, Integer, String> form = (fl, len) -> {
            String ret = Float.toString(fl);
            if (ret.endsWith(".0"))
                ret = ret.substring(0, ret.length() - 2);
            if (ret.length() > len)
                ret = ret.substring(0, len);
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
    public static String getModName(ItemStack stack) {
        String name = GameData.getItemRegistry().getNameForObject(stack.getItem());
        name = name.substring(0, name.indexOf(":"));
        if (name.equals("minecraft"))
            return "Minecraft";
        else
            return dictionary.get(name);
    }

    public static String getModName(FluidStack stack) {
        String name = stack.getFluid().getName();
        if (name.equals("lava") || name.equals("water"))
            return "Minecraft";
        else
            return dictionary.get(stack.getFluid().getStillIcon().getIconName().split(":")[0]);
    }

    public static boolean contains(String s1, String s2) {
        return s1.contains(s2);
    }

    public static class Timer {
        long time = System.currentTimeMillis();
        boolean running = false;

        public void setState(boolean b) {
            if (!b && running)
                running = false;
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
            if (current < 0)
                current += (-current) / total * total + total;
            else
                current = current % total;
            return this;
        }

        public int current() {
            return current;
        }

        public Circulator set(int index) {
            if (index >= 0 && index < total)
                current = index;
            else
                throw new RuntimeException(String.format("Expected: [0, %d), given: %d.", total, index));
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
            while (i.hasNext())
                i.next();
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

    @SideOnly(Side.CLIENT)
    public static class L18n {
        public static Pair<String, Boolean> search(String translateKey, Object... parameters) {
            Pair<String, Boolean> ret = new Pair<>(null, null);
            String buffer = I18n.format(translateKey, parameters);
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
        int limit;

        public Recent(int limit) {
            this.limit = limit;
        }

        public void push(T obj) {
            data.removeIf(t -> t.equals(obj));
            data.push(obj);
            if (data.size() > limit)
                data.pop();
        }

        public List<T> toList() {
            //noinspection unchecked
            return (List<T>) data.clone();
        }
    }

    /**
     * Immutable non-duplicated list builder
     */
    public static class INDListBuilder<T> {
        public ImmutableList.Builder<T> builder = ImmutableList.builder();
        public HashSet<T> set = new HashSet<>();

        public void add(T obj) {
            if (set.add(obj)) builder.add(obj);
        }

        public ImmutableList<T> build() {
            return builder.build();
        }
    }

    public static class OrderedHashMap<K, V> {
        public HashMap<K, Integer> index = new HashMap<>();
        public List<Pair<K, V>> value = new ArrayList<>();

        public void put(K key, V value) {
            if (index.containsKey(key)) this.value.set(index.get(key), new Pair<>(key, value));
            else {
                index.put(key, this.value.size());
                this.value.add(new Pair<>(key, value));
            }
        }

        public Optional<V> get(int index) {
            Pair<K, V> p = value.get(index);
            return Optional.ofNullable(p == null ? null : p.two);
        }

        public Optional<V> get(K key) {
            Integer i = index.get(key);
            return Optional.ofNullable(i == null ? null : value.get(i).two);
        }

        public int getIndex(K key) {
            Integer ret = index.get(key);
            return ret == null ? -1 : ret;
        }

        public Optional<K> getKey(int index) {
            Pair<K, V> ret = value.get(index);
            return Optional.ofNullable(ret == null ? null : ret.one);
        }

        public void forEach(BiConsumer<K, V> func) {
            value.forEach(p -> func.accept(p.one, p.two));
        }
    }
}
