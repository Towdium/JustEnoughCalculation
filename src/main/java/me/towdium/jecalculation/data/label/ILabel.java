package me.towdium.jecalculation.data.label;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.labels.LFluidStack;
import me.towdium.jecalculation.data.label.labels.LItemStack;
import me.towdium.jecalculation.data.label.labels.LOreDict;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.gui.IWPicker;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.pickers.PickerItemStack;
import me.towdium.jecalculation.gui.guis.pickers.PickerPlaceholder;
import me.towdium.jecalculation.gui.guis.pickers.PickerSimple;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.Utilities.Relation;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

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

    ILabel decreaseAmount();

    static void initClient() {
        CONVERTER.register(LOreDict::guess);
        EDITOR.register(PickerSimple.FluidStack::new, "fluid_stack", new LFluidStack(1000, FluidRegistry.WATER));
        EDITOR.register(PickerSimple.OreDict::new, "ore_dict", new LOreDict("ingotIron"));
        EDITOR.register(PickerPlaceholder::new, "placeholder", new LPlaceholder("example", 1, true));
        EDITOR.register(PickerItemStack::new, "item_stack",
                        new LItemStack(new ItemStack(Items.iron_pickaxe)).setFMeta(true));
        MERGER.register("itemStack", "itemStack", LItemStack::merge);
        MERGER.register("oreDict", "oreDict", LOreDict::merge);
        MERGER.register("itemStack", "oreDict", LOreDict::merge);
    }

    int getAmount();

    ILabel multiply(int i);

    boolean acceptPercent();

    ILabel setPercent(boolean p);

    static void initServer() {
        DESERIALIZER.register(LFluidStack.IDENTIFIER, LFluidStack::new);
        DESERIALIZER.register(LItemStack.IDENTIFIER, LItemStack::new);
        DESERIALIZER.register(LOreDict.IDENTIFIER, LOreDict::new);
        DESERIALIZER.register(LPlaceholder.IDENTIFIER, LPlaceholder::new);
        DESERIALIZER.register(LEmpty.IDENTIFIER, i -> EMPTY);
    }

    String getAmountString();

    String getDisplayName();

    void getToolTip(List<String> existing, boolean detailed);

    ILabel copy();

    NBTTagCompound toNBTTagCompound();

    String getIdentifier();

    ILabel setAmount(int amount);

    // test two labels are exactly same except amount
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

    /**
     * Provides support to convert between different types of labels.
     * For example, guess oreDict from list of labels.
     * It can also convert ItemStack or FluidStack to ILabel
     */
    class Converter {
        ArrayList<Function<List<ILabel>, List<ILabel>>> handlers = new ArrayList<>();

        public static ILabel from(@Nullable Object o) {
            if (o == null)
                return ILabel.EMPTY;
            else if (o instanceof ItemStack)
                return new LItemStack((ItemStack) o);
            else if (o instanceof FluidStack)
                return new LFluidStack((FluidStack) o);
            else
                throw new RuntimeException("Unrecognized ingredient type: " + o.getClass());
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
        public ILabel decreaseAmount() {
            return this;
        }

        @Override
        public ILabel multiply(int i) {
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
        public void getToolTip(List<String> existing, boolean detailed) {
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
        public static final String KEY_PERCENT = "percent";
        protected int amount;
        protected boolean percent;

        @Override
        public String toString() {
            return getDisplayName() + 'x' + getAmount();
        }

        public Impl(int amount) {
            this.amount = amount;
            percent = false;
        }

        public Impl(Impl lsa) {
            amount = lsa.amount;
            percent = lsa.percent;
        }

        public Impl(NBTTagCompound nbt) {
            amount = nbt.getInteger(KEY_AMOUNT);
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
            setAmount(getAmount() + getMultiplier());
            return this;
        }

        @Override
        public ILabel decreaseAmount() {
            if (getAmount() <= getMultiplier())
                return ILabel.EMPTY;
            else {
                setAmount(getAmount() - getMultiplier());
                return this;
            }
        }

        @Override
        public ILabel multiply(int i) {
            setAmount(i * getAmount());
            return this;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getToolTip(List<String> existing, boolean detailed) {
            if (detailed)
                existing.add(FORMAT_GREY + Utilities.I18n.format("label.common.tooltip.amount", getAmountString()));
        }

        protected static Optional<ILabel> merge(Impl a, Impl b, boolean add) {
            Impl ret = a.copy();
            boolean p = a.percent || b.percent;
            int amountA = a.amount;
            int amountB = b.amount;
            if (p) {
                if (!a.percent)
                    amountA *= 100;
                if (!b.percent)
                    amountB *= 100;
            }
            ret.amount = add ? amountA + amountB : amountA - amountB;
            ret.percent = p;
            return Optional.of(ret.amount == 0 ? ILabel.EMPTY : ret);
        }

        @Override
        public int getAmount() {
            return amount;
        }

        @Override
        public ILabel setAmount(int amount) {
            if (amount == 0)
                return ILabel.EMPTY;
            this.amount = amount;
            return this;
        }

        @Override
        public NBTTagCompound toNBTTagCompound() {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger(KEY_AMOUNT, amount);
            nbt.setBoolean(KEY_PERCENT, percent);
            return nbt;
        }

        @Override
        public boolean acceptPercent() {
            return true;
        }

        public ILabel setPercent(boolean p) {
            if (!acceptPercent())
                throw new UnsupportedOperationException();
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
        public boolean equals(Object obj) {
            return obj instanceof Impl && amount == ((Impl) obj).amount && matches(obj);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getAmountString() {
            return getAmount() == 0 ?
                   "" :
                   (percent ? Utilities.cutNumber(getAmount(), 4) + "%" : Utilities.cutNumber(getAmount(), 5));
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
