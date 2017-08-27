package me.towdium.jecalculation.core.entry;

import me.towdium.jecalculation.core.entry.entries.EntryItemStack;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Single;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
public interface Entry {
    EntryMergerRegistry MERGER = EntryMergerRegistry.INSTANCE;
    EntryRegistry REGISTRY = EntryRegistry.INSTANCE;
    Entry EMPTY = new EntryItemStack(ItemStack.EMPTY, 0);

    int getAmount();

    void setAmount(int amount);

    ItemStack getRepresentation();

    String getAmountString();

    /**
     * Since {@link Entry} merging is bidirectional, it is redundant to
     * implement on both side. So this class is created for merging
     * {@link Entry Entry(s)}.
     * It uses singleton mode. First register merge functions, then use
     * {@link #test(Entry, Entry)} and {@link #merge(Entry, Entry, boolean)}
     * to operate the {@link Entry}.
     * For registering, see {@link EntryRegistry}.
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
    class EntryRegistry {
        public static final String KEY_IDENTIFIER = "identifier";
        public static final String KEY_CONTENT = "content";
        public static final EntryRegistry INSTANCE;

        static {
            INSTANCE = new EntryRegistry();
        }

        private HashMap<Class<? extends Entry>, Pair<String,
                Function<NBTTagCompound, Optional<Entry>>>> clazzToData = new HashMap<>();
        private HashMap<String, Pair<Class<? extends Entry>,
                Function<NBTTagCompound, Optional<Entry>>>> idToData = new HashMap<>();

        private EntryRegistry() {
        }

        public <T extends Entry> void register(
                Class<T> clazz, String identifier, Function<NBTTagCompound, Optional<Entry>> deserializer) {
            clazzToData.put(clazz, new Pair<>(identifier, deserializer));
            idToData.put(identifier, new Pair<>(clazz, deserializer));
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
            Optional<Entry> e = idToData.get(s).two.apply(nbt.getCompoundTag(KEY_CONTENT));
            if (e.isPresent()) return e.get();
            else throw new RuntimeException("Fail to deserialize entry type: " + s);
        }

        public String getIdentifier(Entry e) {
            Pair<String, Function<NBTTagCompound, Optional<Entry>>> p = clazzToData.get(e.getClass());
            if (p == null) throw new RuntimeException("Unregistered entry type: " + e.getClass());
            else return p.one;
        }
    }

    class Relation<T, R> {
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
