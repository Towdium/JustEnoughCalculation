package me.towdium.jecalculation.data.label;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.client.gui.IWPicker;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.guis.pickers.PickerSimple;
import me.towdium.jecalculation.client.gui.guis.pickers.PickerUniversal;
import me.towdium.jecalculation.data.label.labels.LFluidStack;
import me.towdium.jecalculation.data.label.labels.LItemStack;
import me.towdium.jecalculation.data.label.labels.LOreDict;
import me.towdium.jecalculation.data.label.labels.LString;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.Utilities.Relation;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

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
    RegistryMerger MERGER = new RegistryMerger();
    RegistrySerializer DESERIALIZER = new RegistrySerializer();
    RegistryConverterItem CONVERTER_ITEM = new RegistryConverterItem();
    RegistryConverterFluid CONVERTER_FLUID = new RegistryConverterFluid();
    RegistryEditor EDITOR = new RegistryEditor();
    ILabel EMPTY = new LItemStack(ItemStackHelper.EMPTY_ITEM_STACK, 0);

    String FORMAT_BLUE = "\u00A79";
    String FORMAT_GREY = "\u00A78";
    String FORMAT_ITALIC = "\u00A7o";

    ILabel increaseAmount();

    ILabel increaseAmountLarge();

    ILabel decreaseAmount();

    ILabel decreaseAmountLarge();

    String getAmountString();

    @SideOnly(Side.CLIENT)
    String getDisplayName();

    static void initServer() {
        DESERIALIZER.register(LFluidStack.IDENTIFIER, LFluidStack::new);
        DESERIALIZER.register(LItemStack.IDENTIFIER, LItemStack::new);
        DESERIALIZER.register(LOreDict.IDENTIFIER, LOreDict::new);
        DESERIALIZER.register(LString.IDENTIFIER, LString::new);
    }

    List<String> getToolTip(List<String> existing, boolean detailed);

    ILabel copy();

    NBTTagCompound toNBTTagCompound();

    static void initClient() {
        CONVERTER_ITEM.register(LOreDict::guess);
        EDITOR.register(PickerSimple.FluidStack::new, "fluid_stack", new LFluidStack(FluidRegistry.WATER, 1000));
        EDITOR.register(PickerSimple.OreDict::new, "ore_dict", new LOreDict("ingotIron"));
        EDITOR.register(PickerUniversal::new, "string", new LString("example", 1));
        MERGER.register("itemStack", "itemStack", ILabel.RegistryMerger::mergeItemStackNItemStack);
        MERGER.register("oreDict", "oreDict", ILabel.RegistryMerger::mergeOreDictNOreDict);
    }

    String getIdentifier();

    @SideOnly(Side.CLIENT)
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
     * {@link #merge(ILabel, ILabel, boolean)} to operate the {@link ILabel}.
     * For registering, see {@link RegistrySerializer}.
     */
    class RegistryMerger {
        private Relation<String, MergerFunction> functions = new Relation<>();


        private RegistryMerger() {
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
                    return Optional.of(ret);
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

        public Optional<ILabel> merge(ILabel a, ILabel b, boolean add) {
            Optional<ILabel> ret = functions.get(a.getIdentifier(), b.getIdentifier())
                                            .orElse((x, y, f) -> Optional.empty()).merge(a, b, add);
            if (ret.isPresent() && (ret.get() == a || ret.get() == b))
                throw new RuntimeException("Merger should not modify the given label.");
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
    class RegistrySerializer {
        public static final String KEY_IDENTIFIER = "identifier";
        public static final String KEY_CONTENT = "content";

        private HashMap<String, Function<NBTTagCompound, ILabel>> idToData = new HashMap<>();

        private RegistrySerializer() {
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
            ILabel e = idToData.get(s).apply(nbt.getCompoundTag(KEY_CONTENT));
            if (e != ILabel.EMPTY) return e;
            else throw new RuntimeException("Fail to deserialize label type: " + s);
        }

        public NBTTagCompound serialize(ILabel label) {
            NBTTagCompound ret = new NBTTagCompound();
            ret.setString(KEY_IDENTIFIER, label.getIdentifier());
            ret.setTag(KEY_CONTENT, label.toNBTTagCompound());
            return ret;
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

        public void register(Function<List<T>, List<ILabel>> handler) {
            handlers.add(handler);
        }
    }

    class RegistryConverterItem extends RegistryConverter<ItemStack> {
        private RegistryConverterItem() {
        }

        @Override
        public ILabel toLabel(ItemStack ingredient) {
            return new LItemStack(ingredient);
        }
    }

    class RegistryConverterFluid extends RegistryConverter<net.minecraftforge.fluids.FluidStack> {

        private RegistryConverterFluid() {
        }

        @Override
        public ILabel toLabel(net.minecraftforge.fluids.FluidStack ingredient) {
            return new LFluidStack(new net.minecraftforge.fluids.FluidStack(ingredient.getFluid(), 1000), ingredient.amount);
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
}
