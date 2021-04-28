package me.towdium.jecalculation.core.labels;

import com.google.common.base.CaseFormat;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.core.labels.labels.LabelItemStack;
import me.towdium.jecalculation.core.labels.labels.LabelOreDict;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.Utilities.Relation;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
public interface ILabel {
    RegistryEntryMerger MERGER = RegistryEntryMerger.INSTANCE;
    RegistryDeserializer DESERIALIZER = RegistryDeserializer.INSTANCE;
    RegistryConverterItem CONVERTER_ITEM = RegistryConverterItem.INSTANCE;
    RegistryConverterFluid CONVERTER_FLUID = RegistryConverterFluid.INSTANCE;
    RegistryEditor EDITOR = RegistryEditor.INSTANCE;
    ILabel EMPTY = new LabelItemStack(ItemStackHelper.EMPTY_ITEM_STACK, 0);

    String FORMAT_BLUE = "\u00A79";
    String FORMAT_ITALIC = "\u00A7o";

    ILabel increaseAmount();

    ILabel increaseAmountLarge();

    ILabel decreaseAmount();

    ILabel decreaseAmountLarge();

    String getAmountString();

    String getDisplayName();

    default List<String> getToolTip(List<String> existing) {
        return existing;
    }

    default String getIdentifier() {
        String s = this.getClass().getSimpleName();
        if (s.startsWith("Label")) s = s.substring(5);
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, s);
    }

    ILabel copy();

    NBTTagCompound toNBTTagCompound();

    default void drawEntry(JecGui gui, int xPos, int yPos, boolean center) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(center ? xPos - 8 : xPos, center ? yPos - 8 : yPos, 0);
        drawEntry(gui);
        GlStateManager.popMatrix();
    }

    void drawEntry(JecGui gui);

    /**
     * Since {@link ILabel} merging is bidirectional, it is redundant to
     * implement on both side. So this class is created for merging
     * {@link ILabel label(s)}.
     * It uses singleton mode. First registerGuess merge functions, then use
     * {@link #test(ILabel, ILabel)} and {@link #merge(ILabel, ILabel, boolean)}
     * to operate the {@link ILabel}.
     * For registering, see {@link RegistryDeserializer}.
     */
    class RegistryEntryMerger {
        public static final RegistryEntryMerger INSTANCE;

        static {
            INSTANCE = new RegistryEntryMerger();
            // registerGuess functions here
        }

        private Relation<String, MergerFunction> functions = new Relation<>();


        private RegistryEntryMerger() {
        }

        public void register(String a, String b, MergerFunction func) {
            functions.add(a, b, func);
        }

        public Pair<ILabel, ILabel> merge(ILabel a, ILabel b, boolean add) {
            return functions.get(a.getIdentifier(), b.getIdentifier())
                    .orElse((x, y, f) -> new Pair<>(x, y)).merge(a, b, add);
        }

        public boolean test(ILabel a, ILabel b) {
            Optional<MergerFunction> f = functions.get(a.getIdentifier(), b.getIdentifier());
            if (!f.isPresent()) return false;
            else {
                Pair<ILabel, ILabel> p = f.get().merge(a, b, true);
                return (!(p.one == a && p.two == b) && !(p.two == a && p.one == a));
            }
        }

        @FunctionalInterface
        public interface MergerFunction {
            /**
             * @param a   an {@link ILabel} to merge
             * @param b   another {@link ILabel} to merge
             * @param add add together or cancel each other
             * @return merged {@link ILabel label(s)} if not changed (no matter order), they cannot merge.
             */
            Pair<ILabel, ILabel> merge(ILabel a, ILabel b, boolean add);
        }
    }

    /**
     * This class is used to registerGuess an {@link ILabel} type.
     * Here you can find the identifier and deserializer of one type.
     * For {@link ILabel} operations, see {@link RegistryEntryMerger}
     */
    class RegistryDeserializer {
        public static final String KEY_IDENTIFIER = "identifier";
        public static final String KEY_CONTENT = "content";
        public static final RegistryDeserializer INSTANCE;

        static {
            INSTANCE = new RegistryDeserializer();

            INSTANCE.register("itemStack", LabelOreDict::new);
        }

        private HashMap<String, Function<NBTTagCompound, ILabel>> idToData = new HashMap<>();

        private RegistryDeserializer() {
        }

        public void register(String identifier, Function<NBTTagCompound, ILabel> deserializer) {
            idToData.put(identifier, deserializer);
        }

        /**
         * @param nbt NBT to deserialize
         * @return the recovered label
         * A typical NBT structure of an {@link ILabel} is as follows:
         * <pre>{@code
         * {
         *     KEY_IDENTIFIER: entry_id
         *     KEY_CONTENT: {
         *         entry_content
         *     }
         * }
         * }</pre>
         */
        public ILabel deserialization(NBTTagCompound nbt) {
            String s = nbt.getString(KEY_IDENTIFIER);
            ILabel e = idToData.get(s).apply(nbt.getCompoundTag(KEY_CONTENT));
            if (e != ILabel.EMPTY) return e;
            else throw new RuntimeException("Fail to deserialize label type: " + s);
        }
    }

    class RegistryConverter<T> {
        List<Function<List<T>, List<ILabel>>> handlersGuess = new ArrayList<>();
        List<Function<T, ILabel>> handlersRaw = new ArrayList<>();

        private RegistryConverter() {
        }

        /**
         * @param ingredient the ingredient, possibly itemStack or FluidStack
         * @return the identical representation
         */
        public ILabel toLabel(T ingredient) {
            return handlersRaw.stream().map(h -> h.apply(ingredient))
                              .filter(Objects::nonNull).findFirst().orElse(ILabel.EMPTY);
        }

        /**
         * @param ingredients the list of ingredient, possibly itemStack or FluidStack
         * @return list of guessed representation, sorted by possibility
         */
        public List<ILabel> toLabel(List<T> ingredients) {
            return new ReversedIterator<>(handlersGuess).stream().flatMap(h -> h.apply(ingredients).stream())
                                                        .collect(Collectors.toList());
        }

        void registerGuess(Function<List<T>, List<ILabel>> handler) {
            handlersGuess.add(handler);
        }

        void registerRaw(Function<T, ILabel> handler) {
            handlersRaw.add(handler);
        }
    }

    class RegistryConverterItem extends RegistryConverter<ItemStack> {
        public static final RegistryConverterItem INSTANCE;

        static {
            INSTANCE = new RegistryConverterItem();

            INSTANCE.registerRaw(RegistryConverterItem::convertRawItemStack);
        }

        private RegistryConverterItem() {
        }

        public static ILabel convertRawItemStack(ItemStack is) {
            return new LabelItemStack(is);
        }
    }

    class RegistryConverterFluid extends RegistryConverter<FluidStack> {
        public static final RegistryConverterFluid INSTANCE;

        static {
            INSTANCE = new RegistryConverterFluid();
        }

        private RegistryConverterFluid() {
        }
    }

    class RegistryEditor {
        public static final RegistryEditor INSTANCE;

        static {
            INSTANCE = new RegistryEditor();

            INSTANCE.register(LabelOreDict.getEditor(), "common.label.ore_dict", new LabelOreDict("ingotIron"));
        }

        private ArrayList<Record> records = new ArrayList<>();

        private RegistryEditor() {
        }

        public void register(IEditor editor, String unlocalizedName, ILabel representation) {
            records.add(new Record(editor, unlocalizedName, representation));
        }

        public List<Record> getRecords() {
            return records;
        }

        public interface IEditor extends IDrawable {
            IEditor setCallback(Consumer<ILabel> callback);
        }

        public static class Record {
            public IEditor editor;
            public String localizeKey;
            public ILabel representation;

            public Record(IEditor editor, String localizeKey, ILabel representation) {
                this.editor = editor;
                this.localizeKey = localizeKey;
                this.representation = representation;
            }
        }
    }
}
