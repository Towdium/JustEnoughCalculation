package me.towdium.jecalculation.core.labels;

import com.google.common.base.CaseFormat;
import me.towdium.jecalculation.client.gui.IWPicker;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.guis.pickers.PickerSimple;
import me.towdium.jecalculation.client.gui.guis.pickers.PickerUniversal;
import me.towdium.jecalculation.core.labels.labels.LabelFluidStack;
import me.towdium.jecalculation.core.labels.labels.LabelItemStack;
import me.towdium.jecalculation.core.labels.labels.LabelOreDict;
import me.towdium.jecalculation.core.labels.labels.LabelUniversal;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.Utilities.Relation;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
public interface ILabel {
    RegistryMerger MERGER = RegistryMerger.INSTANCE;
    RegistryDeserializer DESERIALIZER = RegistryDeserializer.INSTANCE;
    RegistryConverterItem CONVERTER_ITEM = RegistryConverterItem.INSTANCE;
    RegistryConverterFluid CONVERTER_FLUID = RegistryConverterFluid.INSTANCE;
    RegistryEditor EDITOR = RegistryEditor.INSTANCE;
    ILabel EMPTY = new LabelItemStack(ItemStackHelper.EMPTY_ITEM_STACK, 0);

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

    default void drawLabel(JecGui gui, int xPos, int yPos, boolean center) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(center ? xPos - 8 : xPos, center ? yPos - 8 : yPos, 0);
        drawLabel(gui);
        GlStateManager.popMatrix();
    }

    void drawLabel(JecGui gui);

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
            INSTANCE.register("itemStack", "itemStack", RegistryMerger::mergeItemStackNItemStack);
            INSTANCE.register("oreDict", "oreDict", RegistryMerger::mergeOreDictNOreDict);
        }


        private Relation<String, MergerFunction> functions = new Relation<>();


        private RegistryMerger() {
        }

        public void register(String a, String b, MergerFunction func) {
            functions.add(a, b, func);
        }

        static Optional<ILabel> mergeItemStackNItemStack(ILabel a, ILabel b, boolean add) {
            if (a instanceof LabelItemStack && b instanceof LabelItemStack) {
                LabelItemStack lisA = (LabelItemStack) a;
                LabelItemStack lisB = (LabelItemStack) b;
                ItemStack isA = lisA.getItemStack();
                ItemStack isB = lisB.getItemStack();
                if (isA.getItem() == isB.getItem() && isA.getItemDamage() == isB.getItemDamage() &&
                    (isA.getTagCompound() == null ? !isB.hasTagCompound() :
                     isA.getTagCompound().equals(isB.getTagCompound()))) {
                    LabelItemStack ret = new LabelItemStack(lisA.getItemStack(),
                                                            add ? lisA.getAmount() + lisB.getAmount() : lisA.getAmount() - lisB.getAmount());
                    return Optional.of(ret);
                }
            }
            return Optional.empty();
        }

        static Optional<ILabel> mergeOreDictNOreDict(ILabel a, ILabel b, boolean add) {
            if (a instanceof LabelOreDict && b instanceof LabelOreDict) {
                LabelOreDict lodA = (LabelOreDict) a;
                LabelOreDict lodB = (LabelOreDict) b;
                if (lodA.getName().equals(lodB.getName()))
                    return Optional.of(new LabelOreDict(lodA.getName(),
                                                        add ? lodA.getAmount() + lodB.getAmount() : lodA.getAmount() - lodB.getAmount()));
            }
            return Optional.empty();
        }

        public Optional<ILabel> merge(ILabel a, ILabel b, boolean add) {
            Optional<ILabel> ret = functions.get(ILabel.getIdentifier(a), ILabel.getIdentifier(b))
                                            .orElse((x, y, f) -> Optional.empty()).merge(a, b, add);
            if (ret.isPresent() && (ret.get() == a || ret.get() == b))
                throw new RuntimeException("Merger should not modify the given labels.");
            return ret;
        }


        @FunctionalInterface
        public interface MergerFunction {
            /**
             * @param a   an {@link ILabel} to merge
             * @param b   another {@link ILabel} to merge
             * @param add add together or cancel each other
             * @return merged {@link ILabel label(s)} if not changed (no matter order), they cannot merge.
             */
            Optional<ILabel> merge(ILabel a, ILabel b, boolean add);
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

    class RegistryConverterFluid extends RegistryConverter<net.minecraftforge.fluids.FluidStack> {
        public static final RegistryConverterFluid INSTANCE;

        static {
            INSTANCE = new RegistryConverterFluid();
        }

        private RegistryConverterFluid() {
        }

        @Override
        public ILabel toLabel(net.minecraftforge.fluids.FluidStack ingredient) {
            return new LabelFluidStack(new net.minecraftforge.fluids.FluidStack(ingredient.getFluid(), 1000), ingredient.amount);
        }
    }

    class RegistryEditor {
        public static final RegistryEditor INSTANCE;

        static {
            INSTANCE = new RegistryEditor();

            INSTANCE.register(PickerSimple.OreDict::new, "common.label.ore_dict",
                              new LabelOreDict("ingotIron"));
            INSTANCE.register(PickerSimple.FluidStack::new, "common.label.fluid_stack",
                              new LabelFluidStack(FluidRegistry.WATER, 1000));
            INSTANCE.register(PickerUniversal::new, "common.label.universal",
                              new LabelUniversal("example", 1));
        }


        private ArrayList<Record> records = new ArrayList<>();

        private RegistryEditor() {
        }

        public void register(Supplier<IWPicker> editor, String unlocalizedName, ILabel representation) {
            records.add(new Record(editor, unlocalizedName, representation));
        }


        public List<Record> getRecords() {
            return records;
        }

        public static class Record {
            public Supplier<IWPicker> editor;
            public String localizeKey;
            public ILabel representation;

            public Record(Supplier<IWPicker> editor, String localizeKey, ILabel representation) {
                this.editor = editor;
                this.localizeKey = localizeKey;
                this.representation = representation;
            }
        }
    }
}
