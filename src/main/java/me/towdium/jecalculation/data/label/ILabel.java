package me.towdium.jecalculation.data.label;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.labels.LFluidStack;
import me.towdium.jecalculation.data.label.labels.LItemStack;
import me.towdium.jecalculation.data.label.labels.LOreDict;
import me.towdium.jecalculation.data.label.labels.LString;
import me.towdium.jecalculation.gui.IWPicker;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.pickers.PickerSimple;
import me.towdium.jecalculation.gui.guis.pickers.PickerUniversal;
import me.towdium.jecalculation.utils.Utilities.Relation;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
    Merger MERGER = new Merger();
    Serializer DESERIALIZER = new Serializer();
    Converter CONVERTER = new Converter();
    RegistryEditor EDITOR = new RegistryEditor();
    ILabel EMPTY = new LEmpty();

    String FORMAT_BLUE = "\u00A79";
    String FORMAT_GREY = "\u00A78";
    String FORMAT_ITALIC = "\u00A7o";

    ILabel increaseAmount();

    ILabel increaseAmountLarge();

    ILabel decreaseAmount();

    ILabel decreaseAmountLarge();

    ILabel invertAmount();

    String getAmountString();

    @SideOnly(Side.CLIENT)
    String getDisplayName();

    static void initServer() {
        DESERIALIZER.register(LFluidStack.IDENTIFIER, LFluidStack::new);
        DESERIALIZER.register(LItemStack.IDENTIFIER, LItemStack::new);
        DESERIALIZER.register(LOreDict.IDENTIFIER, LOreDict::new);
        DESERIALIZER.register(LString.IDENTIFIER, LString::new);
        DESERIALIZER.register(LEmpty.IDENTIFIER, i -> EMPTY);
    }

    List<String> getToolTip(List<String> existing, boolean detailed);

    ILabel copy();

    NBTTagCompound toNBTTagCompound();

    static void initClient() {
        CONVERTER.register(LOreDict::guess);
        EDITOR.register(PickerSimple.FluidStack::new, "fluid_stack", new LFluidStack(FluidRegistry.WATER, 1000));
        EDITOR.register(PickerSimple.OreDict::new, "ore_dict", new LOreDict("ingotIron"));
        EDITOR.register(PickerUniversal::new, "string", new LString("example", 1));
        MERGER.register("itemStack", "itemStack", Merger::mergeItemStackNItemStack);
        MERGER.register("oreDict", "oreDict", Merger::mergeOreDictNOreDict);
    }

    String getIdentifier();

    @SideOnly(Side.CLIENT)
    default void drawLabel(JecaGui gui, int xPos, int yPos, boolean center) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(center ? xPos - 8 : xPos, center ? yPos - 8 : yPos, 0);
        drawLabel(gui);
        GlStateManager.popMatrix();
    }

    void drawLabel(JecaGui gui);

    /**
     * Since {@link ILabel} merging is bidirectional, it is redundant to
     * implement on both side. So this class is created for merging
     * {@link ILabel label(s)}.
     * It uses singleton mode. First registerGuess merge functions, then use
     * {@link #merge(ILabel, ILabel, boolean)} to operate the {@link ILabel}.
     * For registering, see {@link Serializer}.
     */
    class Merger {
        private Relation<String, MergerFunction> functions = new Relation<>();

        private Merger() {
        }

        public void register(String a, String b, MergerFunction func) {
            functions.add(a, b, func);
        }

        static Optional<ILabel> mergeItemStackNItemStack(ILabel a, ILabel b, boolean add) {
            if (a instanceof LItemStack && b instanceof LItemStack) {
                LItemStack lisA = (LItemStack) a;
                LItemStack lisB = (LItemStack) b;
                ItemStack isA = lisA.getItemStack();
                ItemStack isB = lisB.getItemStack();
                if (isA.getItem() == isB.getItem() && isA.getItemDamage() == isB.getItemDamage() &&
                        (isA.getTagCompound() == null ? !isB.hasTagCompound() :
                                isA.getTagCompound().equals(isB.getTagCompound()))) {
                    LItemStack ret = new LItemStack(lisA.getItemStack(),
                            add ? lisA.getAmount() + lisB.getAmount() : lisA.getAmount() - lisB.getAmount());
                    return Optional.of(ret.getAmount() == 0 ? ILabel.EMPTY : ret);
                }
            }
            return Optional.empty();
        }

        static Optional<ILabel> mergeOreDictNOreDict(ILabel a, ILabel b, boolean add) {
            if (a instanceof LOreDict && b instanceof LOreDict) {
                LOreDict lodA = (LOreDict) a;
                LOreDict lodB = (LOreDict) b;
                if (lodA.getName().equals(lodB.getName()))
                    return Optional.of(new LOreDict(lodA.getName(),
                            add ? lodA.getAmount() + lodB.getAmount() : lodA.getAmount() - lodB.getAmount()));
            }
            return Optional.empty();
        }

        public Optional<Pair<ILabel, ILabel>> merge(ILabel a, ILabel b, boolean add) {
            Optional<ILabel> ret = functions.get(a.getIdentifier(), b.getIdentifier())
                    .orElse((x, y, f) -> Optional.empty()).merge(a, b, add);
            if (ret.isPresent() && ret.get() != ILabel.EMPTY && (ret.get() == a || ret.get() == b))
                throw new RuntimeException("Merger should not modify the given label.");
            return ret.map(i -> new Pair<>(i, add ? b.copy().invertAmount() : b.copy()));
        }

        @FunctionalInterface
        public interface MergerFunction {
            /**
             * @param a   an {@link ILabel} to merge
             * @param b   another {@link ILabel} to merge
             * @param add add together or cancel each other
             * @return an optional {@link Pair pair} of {@link ILabel label(s)}.
             * If empty, the labels cannot merge, otherwise returns difference and common elements
             */
            Optional<ILabel> merge(ILabel a, ILabel b, boolean add);
        }
    }

    /**
     * This class is used to registerGuess an {@link ILabel} type.
     * Here you can find the identifier and deserializer of one type.
     * For {@link ILabel} operations, see {@link Merger}
     */
    class Serializer {
        public static final String KEY_IDENTIFIER = "identifier";
        public static final String KEY_CONTENT = "content";

        private HashMap<String, Function<NBTTagCompound, ILabel>> idToData = new HashMap<>();

        private Serializer() {
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
        public ILabel deserialize(NBTTagCompound nbt) {
            String s = nbt.getString(KEY_IDENTIFIER);
            return idToData.get(s).apply(nbt.getCompoundTag(KEY_CONTENT));
        }

        public NBTTagCompound serialize(ILabel label) {
            NBTTagCompound ret = new NBTTagCompound();
            ret.setString(KEY_IDENTIFIER, label.getIdentifier());
            ret.setTag(KEY_CONTENT, label.toNBTTagCompound());
            return ret;
        }
    }

    class Converter {
        ArrayList<Function<List<ILabel>, List<ILabel>>> handlers = new ArrayList<>();

        public static ILabel from(@Nullable Object o) {
            if (o == null) return ILabel.EMPTY;
            else if (o instanceof ItemStack) return new LItemStack((ItemStack) o);
            else if (o instanceof FluidStack) return new LFluidStack((FluidStack) o);
            else throw new RuntimeException("Unrecognized ingredient type: " + o.getClass());
        }

        public void register(Function<List<ILabel>, List<ILabel>> handler) {
            handlers.add(handler);
        }

        public ILabel first(List<ILabel> labels) {
            List<ILabel> guess = guess(labels);
            return guess.isEmpty() ? labels.get(0) : guess.get(0);
        }

        public List<ILabel> guess(List<ILabel> labels) {
            return new ReversedIterator<>(handlers).stream().flatMap(h -> h.apply(labels).stream())
                    .collect(Collectors.toList());
        }
    }

    class RegistryEditor {

        private ArrayList<Record> records = new ArrayList<>();

        private RegistryEditor() {
        }

        public void register(Supplier<IWPicker> editor, String unlocalizedName, ILabel representation) {
            records.add(new Record(editor, "common.label." + unlocalizedName, representation));
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

    class LEmpty implements ILabel {
        public static final String IDENTIFIER = "empty";

        private LEmpty() {
        }

        @Override
        public ILabel increaseAmount() {
            return this;
        }

        @Override
        public ILabel increaseAmountLarge() {
            return this;
        }

        @Override
        public ILabel decreaseAmount() {
            return this;
        }

        @Override
        public ILabel decreaseAmountLarge() {
            return this;
        }

        @Override
        public ILabel invertAmount() {
            return this;
        }

        @Override
        public String getAmountString() {
            return "";
        }

        @Override
        public String getDisplayName() {
            return "";
        }

        @Override
        public List<String> getToolTip(List<String> existing, boolean detailed) {
            return new ArrayList<>();
        }

        @Override
        public ILabel copy() {
            return this;
        }

        @Override
        public NBTTagCompound toNBTTagCompound() {
            return new NBTTagCompound();
        }

        @Override
        public String getIdentifier() {
            return IDENTIFIER;
        }

        @Override
        public void drawLabel(JecaGui gui) {
        }
    }
}
