package me.towdium.jecalculation.core.labels;

import com.google.common.base.CaseFormat;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.drawables.DContainer;
import me.towdium.jecalculation.client.gui.guis.GuiEditorFluidStack;
import me.towdium.jecalculation.core.labels.labels.LabelFluidStack;
import me.towdium.jecalculation.core.labels.labels.LabelItemStack;
import me.towdium.jecalculation.core.labels.labels.LabelOreDict;
import me.towdium.jecalculation.utils.Utilities.Relation;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Single;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface ILabel {
    RegistryMerger MERGER = RegistryMerger.INSTANCE;
    RegistryDeserializer DESERIALIZER = RegistryDeserializer.INSTANCE;
    RegistryConverterItem CONVERTER_ITEM = RegistryConverterItem.INSTANCE;
    RegistryConverterFluid CONVERTER_FLUID = RegistryConverterFluid.INSTANCE;
    RegistryEditor EDITOR = RegistryEditor.INSTANCE;
    ILabel EMPTY = new LabelItemStack(ItemStack.EMPTY, 0);

    String FORMAT_BLUE = "\u00A79";
    String FORMAT_GREY = "\u00A78";
    String FORMAT_ITALIC = "\u00A7o";

    ILabel increaseAmount();

    ILabel increaseAmountLarge();

    ILabel decreaseAmount();

    ILabel decreaseAmountLarge();

    String getAmountString();

    String getDisplayName();

    static String getIdentifier(ILabel c) {
        return getIdentifier(c.getClass());
    }

    static String getIdentifier(Class<? extends ILabel> c) {
        String s = c.getSimpleName();
        if (s.startsWith("Label")) s = s.substring(5);
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, s);
    }

    List<String> getToolTip(List<String> existing, boolean detailed);

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
    class RegistryMerger {
        public static final RegistryMerger INSTANCE;

        static {
            INSTANCE = new RegistryMerger();
            // registerGuess functions here
        }

        private Relation<String, MergerFunction> functions = new Relation<>();


        private RegistryMerger() {
        }

        public void register(String a, String b, MergerFunction func) {
            functions.add(a, b, func);
        }

        public Pair<ILabel, ILabel> merge(ILabel a, ILabel b, boolean add) {
            return functions.get(ILabel.getIdentifier(a), ILabel.getIdentifier(b))
                    .orElse((x, y, f) -> new Pair<>(x, y)).merge(a, b, add);
        }

        public boolean test(ILabel a, ILabel b) {
            Optional<MergerFunction> f = functions.get(ILabel.getIdentifier(a), ILabel.getIdentifier(b));
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
     * For {@link ILabel} operations, see {@link RegistryMerger}
     */
    class RegistryDeserializer {
        public static final String KEY_IDENTIFIER = "identifier";
        public static final String KEY_CONTENT = "content";
        public static final RegistryDeserializer INSTANCE;

        static {
            INSTANCE = new RegistryDeserializer();

            INSTANCE.register(ILabel.getIdentifier(LabelOreDict.class), LabelOreDict::new);
            INSTANCE.register(ILabel.getIdentifier(LabelItemStack.class), LabelItemStack::new);
            INSTANCE.register(ILabel.getIdentifier(LabelFluidStack.class), LabelFluidStack::new);
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

    abstract class RegistryConverter<T> {
        List<Function<List<T>, List<ILabel>>> handlers = new ArrayList<>();

        private RegistryConverter() {
        }

        /**
         * @param ingredient the ingredient, possibly itemStack or FluidStack
         * @return the identical representation
         */
        public abstract ILabel toLabel(T ingredient);

        /**
         * @param ingredients the list of ingredient, possibly itemStack or FluidStack
         * @return list of guessed representation, sorted by possibility
         */
        public List<ILabel> toLabel(List<T> ingredients) {
            return new ReversedIterator<>(handlers).stream().flatMap(h -> h.apply(ingredients).stream())
                    .collect(Collectors.toList());
        }

        void register(Function<List<T>, List<ILabel>> handler) {
            handlers.add(handler);
        }
    }

    class RegistryConverterItem extends RegistryConverter<ItemStack> {
        public static final RegistryConverterItem INSTANCE;

        static {
            INSTANCE = new RegistryConverterItem();

            INSTANCE.register(LabelOreDict::guess);
        }

        private RegistryConverterItem() {
        }

        @Override
        public ILabel toLabel(ItemStack ingredient) {
            return new LabelItemStack(ingredient);
        }
    }

    class RegistryConverterFluid extends RegistryConverter<FluidStack> {
        public static final RegistryConverterFluid INSTANCE;

        static {
            INSTANCE = new RegistryConverterFluid();
        }

        private RegistryConverterFluid() {
        }

        @Override
        public ILabel toLabel(FluidStack ingredient) {
            return new LabelFluidStack(new FluidStack(ingredient.getFluid(), 1000), ingredient.amount);
        }
    }

    class RegistryEditor {
        public static final RegistryEditor INSTANCE;

        static {
            INSTANCE = new RegistryEditor();

            INSTANCE.register(LabelOreDict::getEditor, "common.label.ore_dict",
                    new LabelOreDict("ingotIron"));
            INSTANCE.register(GuiEditorFluidStack::new, "common.label.fluid_stack",
                    new LabelFluidStack(new FluidStack(FluidRegistry.WATER, 1000), 1000));
        }

        private ArrayList<Record> records = new ArrayList<>();

        private RegistryEditor() {
        }

        public void register(Supplier<IEditor> editor, String unlocalizedName, ILabel representation) {
            records.add(new Record(editor, unlocalizedName, representation));
        }

        public List<Record> getRecords() {
            return records;
        }

        public interface IEditor extends IDrawable {
            IEditor setCallback(Consumer<ILabel> callback);
        }

        public static class Editor extends DContainer implements RegistryEditor.IEditor {
            protected Single<Consumer<ILabel>> callback = new Single<>(null);

            @Override
            public RegistryEditor.IEditor setCallback(Consumer<ILabel> callback) {
                this.callback.value = callback;
                return this;
            }
        }

        public static class Record {
            public Supplier<IEditor> editor;
            public String localizeKey;
            public ILabel representation;

            public Record(Supplier<IEditor> editor, String localizeKey, ILabel representation) {
                this.editor = editor;
                this.localizeKey = localizeKey;
                this.representation = representation;
            }
        }
    }
}
