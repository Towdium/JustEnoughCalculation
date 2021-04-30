package me.towdium.jecalculation.data.label;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.IWPicker;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.pickers.PickerSimple;
import me.towdium.jecalculation.gui.guis.pickers.PickerUniversal;
import me.towdium.jecalculation.data.label.labels.LFluidStack;
import me.towdium.jecalculation.data.label.labels.LItemStack;
import me.towdium.jecalculation.data.label.labels.LOreDict;
import me.towdium.jecalculation.data.label.labels.LString;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.Utilities.Relation;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
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

    int getAmount();

    static void initClient() {
        CONVERTER.register(LOreDict::guess);
        EDITOR.register(PickerSimple.FluidStack::new, "fluid_stack", new LFluidStack(1000, FluidRegistry.WATER));
        EDITOR.register(PickerSimple.OreDict::new, "ore_dict", new LOreDict("ingotIron"));
        EDITOR.register(PickerUniversal::new, "string", new LString("example", 1));
        MERGER.register("itemStack", "itemStack", LItemStack::merge);
        MERGER.register("oreDict", "oreDict", LOreDict::mergeOreDict);
    }

    static void initServer() {
        DESERIALIZER.register(LFluidStack.IDENTIFIER, LFluidStack::new);
        DESERIALIZER.register(LItemStack.IDENTIFIER, LItemStack::new);
        DESERIALIZER.register(LOreDict.IDENTIFIER, LOreDict::new);
        DESERIALIZER.register(LString.IDENTIFIER, LString::new);
        DESERIALIZER.register(LEmpty.IDENTIFIER, i -> EMPTY);
    }

    String getAmountString();

    String getDisplayName();

    List<String> getToolTip(List<String> existing, boolean detailed);

    ILabel copy();

    NBTTagCompound toNBTTagCompound();

    String getIdentifier();

    ILabel setAmount(int amount);

    boolean matches(Object l);

    @SideOnly(Side.CLIENT)
    void drawLabel(JecaGui gui, int xPos, int yPos, boolean center);


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

        public Optional<ILabel> merge(ILabel a, ILabel b, boolean add) {
            Optional<ILabel> ret = functions.get(a.getIdentifier(), b.getIdentifier())
                                            .orElse((x, y, f) -> Optional.empty()).merge(a, b, add);
            if (ret.isPresent() && ret.get() != ILabel.EMPTY && (ret.get() == a || ret.get() == b))
                throw new RuntimeException("Merger should not modify the given label.");
            return ret;
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

        @Override
        public boolean matches(Object l) {
            return l == this;
        }

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
        public int getAmount() {
            return 0;
        }

        @Override
        public ILabel setAmount(int amount) {
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
        public void drawLabel(JecaGui gui, int xPos, int yPos, boolean center) {
        }
    }

    abstract class Impl implements ILabel {
        public static final String KEY_AMOUNT = "amount";
        protected int amount;

        @Override
        public String toString() {
            return getDisplayName() + 'x' + getAmount();
        }

        public Impl(int amount) {
            this.amount = amount;
        }

        public Impl(Impl lsa) {
            this(lsa.amount);
        }

        public Impl(NBTTagCompound nbt) {
            amount = nbt.getInteger(KEY_AMOUNT);
        }

        protected int getMultiplier() {
            return 1;
        }

        @Override
        public void drawLabel(JecaGui gui, int xPos, int yPos, boolean center) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(center ? xPos - 8 : xPos, center ? yPos - 8 : yPos, 0);
            drawLabel(gui);
            GlStateManager.popMatrix();
        }

        @Override
        public ILabel increaseAmount() {
            setAmount(getAmount() + getMultiplier());
            return this;
        }

        @Override
        public ILabel increaseAmountLarge() {
            setAmount(getAmount() + 10 * getMultiplier());
            return this;
        }

        @Override
        public ILabel decreaseAmount() {
            if (getAmount() <= getMultiplier()) return ILabel.EMPTY;
            else {
                setAmount(getAmount() - getMultiplier());
                return this;
            }
        }

        @Override
        public ILabel decreaseAmountLarge() {
            if (getAmount() <= 10 * getMultiplier()) return ILabel.EMPTY;
            else {
                setAmount(getAmount() - 10 * getMultiplier());
                return this;
            }
        }

        @Override
        public ILabel invertAmount() {
            setAmount(-getAmount());
            return this;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public List<String> getToolTip(List<String> existing, boolean detailed) {
            if (detailed) existing.add(FORMAT_GREY +
                                       Utilities.I18n.format("label.common.tooltip.amount", getAmountString()));
            return existing;
        }

        @Override
        public NBTTagCompound toNBTTagCompound() {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger(KEY_AMOUNT, amount);
            return nbt;
        }

        @Override
        public int getAmount() {
            return amount;
        }

        @Override
        public ILabel setAmount(int amount) {
            if (amount == 0) return ILabel.EMPTY;
            this.amount = amount;
            return this;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getAmountString() {
            return getAmount() == 0 ? "" : Utilities.cutNumber(getAmount(), 5);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Impl && amount == ((Impl) obj).amount && matches(obj);
        }

        abstract protected void drawLabel(JecaGui gui);
    }
}
