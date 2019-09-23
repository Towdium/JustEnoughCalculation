package me.towdium.jecalculation.data.label;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.labels.LFluidStack;
import me.towdium.jecalculation.data.label.labels.LItemStack;
import me.towdium.jecalculation.data.label.labels.LOreDict;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.pickers.IPicker;
import me.towdium.jecalculation.gui.guis.pickers.PickerItemStack;
import me.towdium.jecalculation.gui.guis.pickers.PickerPlaceholder;
import me.towdium.jecalculation.gui.guis.pickers.PickerSimple;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import me.towdium.jecalculation.utils.wrappers.Pair;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraft.item.ItemEnchantedBook.getEnchantedItemStack;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface ILabel {
    Merger MERGER = new Merger();
    Serializer SERIALIZER = new Serializer();
    Converter CONVERTER = new Converter();
    RegistryEditor EDITOR = new RegistryEditor();
    ILabel EMPTY = new LEmpty();

    String FORMAT_BLUE = "\u00A79";
    String FORMAT_GREY = "\u00A78";
    String FORMAT_ITALIC = "\u00A7o";

    @Nullable
    Object getRepresentation();

    ILabel increaseAmount();

    ILabel decreaseAmount();

    static void initClient() {
        CONVERTER.register(LItemStack::suggest, Converter.Priority.SUGGEST);
        CONVERTER.register(LOreDict::suggest, Converter.Priority.SUGGEST);
        CONVERTER.register(LFluidStack::suggest, Converter.Priority.SUGGEST);
        CONVERTER.register(LItemStack::fallback, Converter.Priority.FALLBACK);
        CONVERTER.register(LOreDict::fallback, Converter.Priority.FALLBACK);
        EDITOR.register(PickerSimple.FluidStack::new, "fluid", new LFluidStack(1000, FluidRegistry.WATER));
        EDITOR.register(PickerSimple.OreDict::new, "ore", new LOreDict("ingotIron"));
        EDITOR.register(PickerPlaceholder::new, "placeholder", new LPlaceholder("example", 1, true));
        EDITOR.register(PickerItemStack::new, "item", new LItemStack(new ItemStack(Items.IRON_PICKAXE)).setFMeta(true));
        MERGER.register("itemStack", "itemStack", Impl.form(LItemStack.class, LItemStack.class, LItemStack::merge));
        MERGER.register("oreDict", "oreDict", Impl.form(LOreDict.class, LOreDict.class, LOreDict::mergeSame));
        MERGER.register("oreDict", "itemStack", Impl.form(LOreDict.class, LItemStack.class, LOreDict::mergeFuzzy));
        MERGER.register("fluidStack", "fluidStack", Impl.form(LFluidStack.class, LFluidStack.class, LFluidStack::merge));
        MERGER.register("placeholder", "placeholder", Impl.form(LPlaceholder.class, LPlaceholder.class, LPlaceholder::merge));
    }

    long getAmount();

    ILabel multiply(float i);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean acceptPercent();

    @SuppressWarnings("UnusedReturnValue")
    ILabel setPercent(boolean p);

    boolean isPercent();

    static void initServer() {
        SERIALIZER.register(LFluidStack.IDENTIFIER, LFluidStack::new);
        SERIALIZER.register(LItemStack.IDENTIFIER, LItemStack::new);
        SERIALIZER.register(LOreDict.IDENTIFIER, LOreDict::new);
        SERIALIZER.register(LPlaceholder.IDENTIFIER, LPlaceholder::new);
        SERIALIZER.register(LEmpty.IDENTIFIER, i -> EMPTY);
    }

    String getAmountString(boolean round);

    String getDisplayName();

    void getToolTip(List<String> existing, boolean detailed);

    ILabel copy();

    NBTTagCompound toNbt();

    String getIdentifier();

    ILabel setAmount(long amount);

    // test two labels are exactly same except amount
    boolean matches(Object l);

    @SideOnly(Side.CLIENT)
    void drawLabel(JecaGui gui, int xPos, int yPos, boolean center);

    /**
     * Since {@link ILabel} merging is bidirectional, it is redundant to
     * implement on both side. So this class is created for merging
     * {@link ILabel label(s)}.
     * It uses singleton mode. First register merge functions, then use
     * {@link #merge(ILabel, ILabel)} to operate the {@link ILabel}.
     * For registering, see {@link Serializer}.
     */
    class Merger {
        private Utilities.Relation<String, MergerFunction> functions = new Utilities.Relation<>();

        private Merger() {
        }

        public void register(String a, String b, MergerFunction func) {
            functions.put(a, b, func);
        }

        /**
         * @param a one label
         * @param b another label
         * @return merge result or empty
         * This function will try to merge two labels.
         * If both label has same type, just sum up amount
         * For different type, the framework will try reversing the order for MergeFunctions to work
         * So generally speaking, a and b has no priority in this function
         */
        public Optional<ILabel> merge(ILabel a, ILabel b) {
            MergerFunction mf = functions.get(a.getIdentifier(), b.getIdentifier());
            if (mf == null) return Optional.empty();
            return Optional.ofNullable(mf.merge(a, b));
        }

        @FunctionalInterface
        public interface MergerFunction {
            /**
             * @param a requested label
             * @param b supplied label
             * @return merge result or empty
             * This function ensures to generate same type if a and b are from same type
             * If a is fuzzy, b is explicit, generates a is amount is negative, else generates b
             * Normally, explicit type cannot request fuzzy type
             */
            @Nullable
            ILabel merge(ILabel a, ILabel b);
        }
    }

    /**
     * This class is used to registerGuess an {@link ILabel} type.
     * Here you can find the identifier and deserializer of one type.
     * For {@link ILabel} operations, see {@link Merger}
     */
    class Serializer {
        public static final String KEY_IDENTIFIER = "type";
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
            Function<NBTTagCompound, ILabel> func = idToData.get(s);
            if (func == null) JustEnoughCalculation.logger.warn("Unrecognized identifier \"" + s + "\", abort");
            else try {
                return func.apply(nbt.getCompoundTag(KEY_CONTENT));
            } catch (SerializationException ignored) {
            }
            return EMPTY;
        }

        public NBTTagCompound serialize(ILabel label) {
            NBTTagCompound ret = new NBTTagCompound();
            ret.setString(KEY_IDENTIFIER, label.getIdentifier());
            ret.setTag(KEY_CONTENT, label.toNbt());
            return ret;
        }

        public static class SerializationException extends RuntimeException {
            public SerializationException(String s) {
                super(s);
                JustEnoughCalculation.logger.warn(s);
            }
        }
    }

    /**
     * Provides support to convert between different types of labels.
     * For example, guess oreDict from list of labels.
     * It can also convert ItemStack or FluidStack to ILabel
     */
    class Converter {
        static EnumMap<Priority, ArrayList<ConverterFunction>> handlers;

        public enum Priority {SUGGEST, FALLBACK}

        static {
            handlers = new EnumMap<>(Priority.class);
            handlers.put(Priority.SUGGEST, new ArrayList<>());
            handlers.put(Priority.FALLBACK, new ArrayList<>());
        }

        public static ILabel from(@Nullable Object o) {
            if (o == null) return ILabel.EMPTY;
            else if (o instanceof ItemStack) return new LItemStack((ItemStack) o);
            else if (o instanceof FluidStack) return new LFluidStack((FluidStack) o);
            else if (o instanceof EnchantmentData) return new LItemStack(getEnchantedItemStack((EnchantmentData) o));
            else throw new RuntimeException("Unrecognized ingredient type: " + o.getClass());
        }

        public void register(ConverterFunction handler, Priority priority) {
            handlers.get(priority).add(handler);
        }

        // get most possible guess from labels
        public ILabel first(List<ILabel> labels, @Nullable IRecipeLayout context) {
            List<ILabel> guess = guess(labels, context).one;
            return guess.isEmpty() ? labels.get(0) : guess.get(0);
        }

        public ILabel first(List<ILabel> labels) {
            return first(labels, null);
        }

        // to test if the labels can be converted to other labels (like oreDict)
        public Pair<List<ILabel>, List<ILabel>> guess(List<ILabel> labels) {
            return guess(labels, null);
        }

        public Pair<List<ILabel>, List<ILabel>> guess(List<ILabel> labels, @Nullable IRecipeLayout context) {
            List<ILabel> ret = new ArrayList<>();
            List<ILabel> suggest = new ReversedIterator<>(handlers.get(Priority.SUGGEST)).stream()
                    .flatMap(h -> h.convert(labels, context).stream())
                    .collect(Collectors.toList());
            List<ILabel> fallback = new ReversedIterator<>(handlers.get(Priority.FALLBACK)).stream()
                    .flatMap(h -> h.convert(labels, context).stream())
                    .collect(Collectors.toList());
            return new Pair<>(suggest, fallback);
        }

        @FunctionalInterface
        public interface ConverterFunction {
            List<ILabel> convert(List<ILabel> a, @Nullable IRecipeLayout context);
        }
    }

    class RegistryEditor {

        private ArrayList<Record> records = new ArrayList<>();

        private RegistryEditor() {
        }

        public void register(Supplier<IPicker> editor, String unlocalizedName, ILabel representation) {
            records.add(new Record(editor, "common.label." + unlocalizedName, representation));
        }

        public List<Record> getRecords() {
            return records;
        }

        public static class Record {
            public Supplier<IPicker> editor;
            public String localizeKey;
            public ILabel representation;

            public Record(Supplier<IPicker> editor, String localizeKey, ILabel representation) {
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

        @Nullable
        @Override
        public Object getRepresentation() {
            return null;
        }

        @Override
        public ILabel increaseAmount() {
            return this;
        }

        @Override
        public ILabel decreaseAmount() {
            return this;
        }

        @Override
        public ILabel multiply(float i) {
            return this;
        }

        @Override
        public boolean acceptPercent() {
            return false;
        }

        @Override
        public ILabel setPercent(boolean p) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPercent() {
            return false;
        }

        @Override
        public long getAmount() {
            return 0;
        }

        @Override
        public ILabel setAmount(long amount) {
            return this;
        }

        @Override
        public String getAmountString(boolean round) {
            return "";
        }

        @Override
        public String getDisplayName() {
            return "";
        }

        @Override
        public void getToolTip(List<String> existing, boolean detailed) {
        }

        @Override
        public ILabel copy() {
            return this;
        }

        @Override
        public NBTTagCompound toNbt() {
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
        public static final String KEY_PERCENT = "percent";
        protected long amount;
        protected boolean percent;

        @Override
        public String toString() {
            return getDisplayName() + 'x' + getAmount();
        }

        public Impl(long amount, boolean percent) {
            this.amount = amount;
            this.percent = percent;
        }

        public Impl(Impl lsa) {
            amount = lsa.amount;
            percent = lsa.percent;
        }

        public Impl(NBTTagCompound nbt) {
            amount = nbt.getLong(KEY_AMOUNT);
            percent = nbt.getBoolean(KEY_PERCENT);
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
            return setAmount(getAmount() + getMultiplier());
        }

        @Override
        public ILabel decreaseAmount() {
            if (getAmount() <= getMultiplier()) return ILabel.EMPTY;
            else {
                return setAmount(getAmount() - getMultiplier());
            }
        }

        @Override
        public int hashCode() {
            return (int) (amount ^ (percent ? 1 : 0));
        }

        protected static Merger.MergerFunction form(Class a, Class b, BiPredicate<ILabel, ILabel> p) {
            return (c, d) -> {
                if (a.isInstance(d) && b.isInstance(c)) {
                    ILabel tmp = c;
                    c = d;
                    d = tmp;
                }
                if (a.isInstance(c) && b.isInstance(d) && p.test(c, d)) {
                    long amountC = c.isPercent() ? c.getAmount() : Math.multiplyExact(c.getAmount(), 100);
                    long amountD = d.isPercent() ? d.getAmount() : Math.multiplyExact(d.getAmount(), 100);
                    long amount = Math.addExact(amountC, amountD);
                    long amountI = (amount > 0 ? Math.addExact(amount, 99) : Math.subtractExact(amount, 99)) / 100;
                    if (amount == 0) return EMPTY;
                    else if (amount > 0) return d.copy().setAmount(d.isPercent() ? amount : amountI);
                    else return c.copy().setAmount(c.isPercent() ? amount : amountI);
                } else return null;
            };
        }

        @Override
        public ILabel multiply(float i) {
            float amount = i * getAmount();
            if (amount > Long.MAX_VALUE) throw new ArithmeticException("Multiply overflow");
            return setAmount((long) amount);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getToolTip(List<String> existing, boolean detailed) {
            if (detailed) existing.add(FORMAT_GREY +
                    Utilities.I18n.get("label.common.amount", getAmountString(false)));
        }

        @Override
        public long getAmount() {
            return amount;
        }

        @Override
        public ILabel setAmount(long amount) {
            if (amount == 0) return ILabel.EMPTY;
            this.amount = amount;
            return this;
        }

        @Override
        public NBTTagCompound toNbt() {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setLong(KEY_AMOUNT, amount);
            if (percent) nbt.setBoolean(KEY_PERCENT, true);
            return nbt;
        }

        @Override
        public boolean acceptPercent() {
            return true;
        }

        public ILabel setPercent(boolean p) {
            if (p && !acceptPercent()) throw new UnsupportedOperationException();
            if (p && !percent) {
                amount *= 100;
                percent = true;
            } else if (!p && percent) {
                amount = (amount + 99) / 100;
                percent = false;
            }
            return this;
        }

        @Override
        public boolean isPercent() {
            return percent;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Impl && amount == ((Impl) obj).amount && matches(obj);
        }

        @Override
        @SideOnly(Side.CLIENT)
        @SuppressWarnings("IntegerDivisionInFloatingPointContext")
        public String getAmountString(boolean round) {
            if (getAmount() == 0) return "";
            else if (!percent) return Utilities.cutNumber(getAmount(), 5);
            else if (round) return Utilities.cutNumber((getAmount() + 99) / 100, 5);
            else return Utilities.cutNumber(getAmount(), 4) + "%";
        }

        @Override
        abstract public Impl copy();

        @Override
        public boolean matches(Object l) {
            return l instanceof Impl && percent == ((Impl) l).percent;
        }

        abstract protected void drawLabel(JecaGui gui);


    }
}
