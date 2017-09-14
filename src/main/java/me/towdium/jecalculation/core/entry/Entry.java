package me.towdium.jecalculation.core.entry;

import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.core.entry.entries.EntryItemStack;
import me.towdium.jecalculation.utils.Utilities.Relation;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
public interface Entry {
    EntryMergerRegistry MERGER = EntryMergerRegistry.INSTANCE;
    Registry REGISTRY = Registry.INSTANCE;
    Entry EMPTY = new EntryItemStack(ItemStack.EMPTY, 0);

    Entry increaseAmount();

    Entry increaseAmountLarge();

    Entry decreaseAmount();

    Entry decreaseAmountLarge();

    String getAmountString();

    String getDisplayName();

    Entry copy();

    void drawEntry(JecGui gui);

    /**
     * Since {@link Entry} merging is bidirectional, it is redundant to
     * implement on both side. So this class is created for merging
     * {@link Entry Entry(s)}.
     * It uses singleton mode. First register merge functions, then use
     * {@link #test(Entry, Entry)} and {@link #merge(Entry, Entry, boolean)}
     * to operate the {@link Entry}.
     * For registering, see {@link Registry}.
     */
    class EntryMergerRegistry {
        public static final EntryMergerRegistry INSTANCE;

        static {
            INSTANCE = new EntryMergerRegistry();
            // register functions here
        }

        private Relation<String, MergerFunction> functions = new Relation<>();


        private EntryMergerRegistry() {
        }

        public void register(String a, String b, MergerFunction func) {
            functions.add(a, b, func);
        }

        public Pair<Entry, Entry> merge(Entry a, Entry b, boolean add) {
            return functions.get(REGISTRY.getIdentifier(a), REGISTRY.getIdentifier(b))
                    .orElse((x, y, f) -> new Pair<>(x, y)).merge(a, b, add);
        }

        public boolean test(Entry a, Entry b) {
            Optional<MergerFunction> f = functions.get(REGISTRY.getIdentifier(a), REGISTRY.getIdentifier(b));
            if (!f.isPresent()) return false;
            else {
                Pair<Entry, Entry> p = f.get().merge(a, b, true);
                return (!(p.one == a && p.two == b) && !(p.two == a && p.one == a));
            }
        }

        @FunctionalInterface
        public interface MergerFunction {
            /**
             * @param a   an {@link Entry} to merge
             * @param b   another {@link Entry} to merge
             * @param add add together or cancel each other
             * @return merged {@link Entry Entry(s)} if not changed (no matter order), they cannot merge.
             */
            Pair<Entry, Entry> merge(Entry a, Entry b, boolean add);
        }
    }

    /**
     * This class is used to register an {@link Entry} type.
     * Here you can find the identifier and deserializer of one type.
     * For {@link Entry} operations, see {@link EntryMergerRegistry}
     */
    class Registry {
        public static final String KEY_IDENTIFIER = "identifier";
        public static final String KEY_CONTENT = "content";
        public static final Registry INSTANCE;

        static {
            INSTANCE = new Registry();
        }

        private HashMap<Class<? extends Entry>, Record> clazzToData = new HashMap<>();
        private HashMap<String, Record> idToData = new HashMap<>();

        private Registry() {
        }

        public <T extends Entry> void register(
                Class<T> clazz, String identifier, Function<NBTTagCompound, Entry> deserializer,
                IDrawable editor, Entry representation) {
            Record r = new Record(deserializer, clazz, representation, editor, identifier);
            clazzToData.put(clazz, r);
            idToData.put(identifier, r);
        }


        /**
         * @param nbt NBT to deserialize
         * @return the recovered entry
         * A typical NBT structure of an {@link Entry} is as follows:
         * <pre>{@code
         * {
         *     KEY_IDENTIFIER: entry_id
         *     KEY_CONTENT: {
         *         entry_content
         *     }
         * }
         * }</pre>
         */
        public Entry deserialization(NBTTagCompound nbt) {
            String s = nbt.getString(KEY_IDENTIFIER);
            Entry e = idToData.get(s).deserializer.apply(nbt.getCompoundTag(KEY_CONTENT));
            if (e != Entry.EMPTY) return e;
            else throw new RuntimeException("Fail to deserialize entry type: " + s);
        }

        public String getIdentifier(Entry e) {
            Record r = clazzToData.get(e.getClass());
            if (r == null) throw new RuntimeException("Unregistered entry type: " + e.getClass());
            else return r.identifier;
        }

        private static class Record {
            Function<NBTTagCompound, Entry> deserializer;
            Class<? extends Entry> clazz;
            Entry representation;
            IDrawable editor;
            String identifier;

            public Record(Function<NBTTagCompound, Entry> deserializer, Class<? extends Entry> clazz,
                          Entry representation, IDrawable editor, String identifier) {
                this.deserializer = deserializer;
                this.clazz = clazz;
                this.representation = representation;
                this.editor = editor;
                this.identifier = identifier;
            }
        }
    }

    class ConverterRegistry<T> {
        List<Function<List<T>, List<Entry>>> handlers = new ArrayList<>();

        private ConverterRegistry() {
        }

        public Entry toEntry(T ingredient) {
            return handlers.stream().map(h -> h.apply(Collections.singletonList(ingredient)))
                    .filter(l -> !l.isEmpty()).findFirst().orElse(Collections.singletonList(Entry.EMPTY)).get(0);
        }

        public List<Entry> toEntry(List<T> ingredients) {
            return new ReversedIterator<>(handlers).stream().flatMap(h -> h.apply(ingredients).stream())
                    .collect(Collectors.toList());
        }

        void register(Function<List<T>, List<Entry>> handler) {
            handlers.add(handler);
        }
    }

    class ConverterRegistryItem extends ConverterRegistry<ItemStack> {
        public static final ConverterRegistryItem INSTANCE;

        static {
            INSTANCE = new ConverterRegistryItem();

            INSTANCE.register(ConverterRegistryItem::convertRawItemStack);
        }

        private ConverterRegistryItem() {
        }

        public static List<Entry> convertRawItemStack(List<ItemStack> iss) {
            return iss.stream().map(EntryItemStack::new).collect(Collectors.toList());
        }
    }

    class ConverterRegistryFluid extends ConverterRegistry<FluidStack> {
        public static final ConverterRegistryFluid INSTANCE;

        static {
            INSTANCE = new ConverterRegistryFluid();
        }

        private ConverterRegistryFluid() {
        }
    }
}
