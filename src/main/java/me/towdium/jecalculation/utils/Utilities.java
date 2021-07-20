package me.towdium.jecalculation.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.polyfill.NBTHelper;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.minecraft.client.resources.I18n.format;

/**
 * Author: Towdium
 * Date:   2016/6/25.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
@ParametersAreNonnullByDefault
public class Utilities {
    // FLOAT FORMATTING
    public static char[] suffix = new char[]{'K', 'M', 'B', 'G', 'T', 'P'};
    public static DecimalFormat[] format = new DecimalFormat[]{new DecimalFormat("#."),
                                                               new DecimalFormat("#.#"),
                                                               new DecimalFormat("#.##"),
                                                               new DecimalFormat("#.###"),
                                                               new DecimalFormat("#.####")};

    public static String cutNumber(float f, int size) {
        BiFunction<Float, Integer, String> form = (fl, len) -> format[len - 1 - (int) Math.log10(fl)].format(fl);
        int scale = (int) Math.log10(f) / 3;
        if (scale == 0)
            return form.apply(f, size);
        else
            return form.apply(f / (float) Math.pow(1000, scale), size - 1) + suffix[scale - 1];
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Stream<T> stream(Optional<T> o) {
        return o.map(Stream::of).orElse(Stream.empty());
    }

    public static <T> Supplier<T> fake(Runnable r) {
        return () -> {
            r.run();
            return null;
        };
    }

    public static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++)
            sb.append(s);
        return sb.toString();
    }

    // MOD NAME
    @Nullable
    public static String getModName(Item item) {
        String name = getName(item);
        String id = name.substring(0, name.indexOf(":"));
        return id.equals("minecraft") ? "Minecraft" : Loader.instance().getIndexedModList().get(id).getName();
    }

    public static String getName(Item item) {
        return GameData.getItemRegistry().getNameForObject(item);
    }

    public static String getName(ItemStack itemStack) {
        return getName(itemStack.getItem());
    }

    /**
     * If fluid is lava or water, return 'Minecraft'.
     * If has still icon or flowing icon, get the icon name, or return 'unknown';
     * The icon name should be similar to 'modId:fluidId', then get the modId.
     * Use the mod id to find in mod list, if not found, use the capitalized mod id to find in mod list;
     * If still not found, return the capitalized mod id
     * @param fluid fluid
     * @return mod name
     */
    public static String getModName(Fluid fluid) {
        String name = fluid.getName();
        if (name.equals("lava") || name.equals("water"))
            return "Minecraft";
        else {
            IIcon icon = getFluidIcon(fluid);
            if (icon == null) {
                return "Unknown";
            }
            String iconName = icon.getIconName();
            String modId = iconName.split(":")[0];

            Map<String, ModContainer> indexedModList = Loader.instance().getIndexedModList();
            ModContainer modContainer = indexedModList.get(modId);
            if(modContainer == null) {
                String capitalizedModId =WordUtils.capitalize(modId);
                modContainer = indexedModList.get(capitalizedModId);
                if(modContainer == null) {
                    return capitalizedModId;
                }
            }
            return modContainer.getName();
        }
    }



    private static IIcon getFluidIcon(Fluid fluid) {
        IIcon icon = fluid.getFlowingIcon();
        if(icon == null) {
            icon = fluid.getStillIcon();
        }
        return icon;
    }

    public static NBTTagCompound getTag(ItemStack is) {
        return NBTHelper.getOrCreateSubCompound(is, JustEnoughCalculation.Reference.MODID);
    }

    public static EntityClientPlayerMP getPlayer() {
        return Objects.requireNonNull(ClientUtils.mc().thePlayer);
    }


    public static class Relation<K, V> {
        public HashMap<Pair<K, K>, V> data = new HashMap<>();

        public void put(K a, K b, V v) {
            Pair<K, K> pair = new Pair<>(a, b);
            V tmp = data.get(pair);
            data.put(tmp == null ? new Pair<>(b, a) : pair, v);
        }

        @Nullable
        public V get(K a, K b) {
            V ret = data.get(new Pair<>(a, b));
            if (ret == null)
                ret = data.get(new Pair<>(b, a));
            return ret;
        }
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
                current += (total - current - 1) / total * total;
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

    @SideOnly(Side.CLIENT)
    public static class I18n {
        public static boolean contains(String s1, String s2) {
            return s1.contains(s2);
        }

        public static Pair<String, Boolean> search(String translateKey, Object... parameters) {
            Pair<String, Boolean> ret = new Pair<>(null, null);
            translateKey = "jecalculation." + translateKey;
            String buffer = format(translateKey, parameters);
            ret.two = !buffer.equals(translateKey);
            buffer = StringEscapeUtils.unescapeJava(buffer);
            ret.one = buffer.replace("\t", "    ");
            return ret;
        }

        public static String get(String translateKey, Object... parameters) {
            return search(translateKey, parameters).one;
        }

        public static List<String> wrap(String s, int width) {
            return new TextWrapper()
                    .wrap(s, ClientUtils.mc().getLanguageManager().getCurrentLanguage().getLanguageCode(),
                          i -> TextWrapper.renderer.getCharWidth(i), width);
        }

        static class TextWrapper {
            static FontRenderer renderer = ClientUtils.mc().fontRenderer;

            String str;
            BreakIterator it;
            List<String> temp = new ArrayList<>();
            Function<Character, Integer> func;
            int start, end, section, space, cursor, width;

            private void cut() {
                char c = str.charAt(cursor);
                if (c == '\f')
                    cursor++;
                temp.add(str.substring(start, cursor));
                if (c == ' ' || c == '　' || c == '\n')
                    cursor++;
                start = cursor;
                end = cursor;
                space = width;
                section = func.apply(str.charAt(cursor));
            }

            private void move() {
                temp.add(str.substring(start, end));
                start = end;
                space = width;
            }

            private List<String> wrap(String s, String languageCode, Function<Character, Integer> func, int width) {
                Locale l = getLocaleFromString(languageCode);
                temp.clear();
                start = 0;
                end = 0;
                cursor = 0;
                space = width;
                str = s;
                it = BreakIterator.getLineInstance(l);
                it.setText(s);
                this.width = width;
                this.func = func;
                for (int i = it.next(); i != BreakIterator.DONE; i = it.next()) {
                    for (cursor = end; cursor < i; cursor++) {
                        char ch = str.charAt(cursor);
                        section += func.apply(str.charAt(cursor));
                        if (ch == '\n' || ch == '\f')
                            cut();
                        else if (section > space) {
                            if (start == end)
                                cut();
                            else
                                move();
                        }
                    }
                    space -= section;
                    section = 0;
                    end = cursor;
                }
                move();
                return temp;
            }
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

        public boolean push(T obj, boolean replace) {
            if (replace)
                data.pop();
            boolean ret = data.removeIf(t -> tester != null ? tester.test(t, obj) : t.equals(obj));
            data.addFirst(obj);
            if (data.size() > limit)
                data.removeLast();
            return ret;
        }

        public List<T> toList() {
            return new ArrayList<>(data);
        }

        public void clear() {
            data.clear();
        }

        public int size() {
            return data.size();
        }
    }

    public static class Json {
        @Nullable
        public static NBTTagCompound read(File f) {
            try {
                String s = FileUtils.readFileToString(f, "UTF-8");
                return read(s);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Nullable
        public static NBTTagCompound read(String s) {
            JsonParser parser = new JsonParser();
            try {
                JsonElement element = parser.parse(s);
                return (NBTTagCompound) NBTJson.toNbt(element);
            } catch (IllegalArgumentException | JsonSyntaxException e) {
                e.printStackTrace();
                JustEnoughCalculation.logger.error("Failed to load json to nbt");
                return null;
            }
        }

        public static void write(NBTTagCompound nbt, File f) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                fos.write(write(nbt).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public static String write(NBTTagCompound nbt) {
            Writer w = new Writer();
            write(nbt, w);
            w.enter();
            return w.build();
        }

        private static void write(NBTBase nbt, Writer w) {
            if (nbt instanceof NBTTagCompound) {
                NBTTagCompound tags = (NBTTagCompound) nbt;
                //noinspection unchecked
                Set<String> keySet = tags.func_150296_c();
                boolean wrap = keySet.size() > 1;
                boolean first = true;
                w.sb.append('{');
                if (wrap)
                    w.indent++;
                for (String i : keySet) {
                    if (first)
                        first = false;
                    else
                        w.sb.append(',');
                    if (wrap)
                        w.enter();
                    w.sb.append('"');
                    w.sb.append(i);
                    w.sb.append("\": ");
                    write(tags.getTag(i), w);
                }
                if (wrap) {
                    w.indent--;
                    w.enter();
                }
                w.sb.append('}');
            } else if (nbt instanceof NBTTagList) {
                NBTTagList tags = (NBTTagList) nbt;
                boolean wrap = tags.tagCount() > 1;
                boolean first = true;
                w.sb.append('[');
                if (wrap)
                    w.indent++;
                //noinspection unchecked
                for (NBTBase i : (List<NBTBase>) tags.tagList) {
                    if (first)
                        first = false;
                    else
                        w.sb.append(',');
                    if (wrap)
                        w.enter();
                    write(i, w);
                }
                if (wrap) {
                    w.indent--;
                    w.enter();
                }
                w.sb.append(']');
            } else
                w.sb.append(nbt);
        }

        private static class Writer {
            public int indent = 0;
            StringBuilder sb = new StringBuilder();

            public void enter() {
                sb.append('\n');
                char[] tmp = new char[4 * indent];
                Arrays.fill(tmp, ' ');
                sb.append(tmp);
            }

            public String build() {
                return sb.toString();
            }
        }
    }

    public static Locale getLocaleFromString(String languageCode) {
        String[] parts = languageCode.split("_", -1);
        if (parts.length == 1)
            return new Locale(parts[0]);
        else if (parts.length == 2 || (parts.length == 3 && parts[2].startsWith("#")))
            return new Locale(parts[0], parts[1]);
        else
            return new Locale(parts[0], parts[1], parts[2]);
    }

    public static String[] mergeStringArrays(String[] ...arrays){
        return Stream.of(arrays)
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }
}
