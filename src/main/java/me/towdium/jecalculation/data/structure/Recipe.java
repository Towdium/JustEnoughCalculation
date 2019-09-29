package me.towdium.jecalculation.data.structure;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: towdium
 * Date:   17-10-6.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Recipe {
    public static final String KEY_INPUT = "input";
    public static final String KEY_CATALYST = "catalyst";
    public static final String KEY_OUTPUT = "output";
    static final ILabel[] EMPTY_ARRAY = new ILabel[0];
    ILabel[] input;
    ILabel[] catalyst;
    ILabel[] output;

    public Recipe(CompoundNBT nbt) {
        this(readNbtList(nbt.getList(KEY_INPUT, 10)),
                readNbtList(nbt.getList(KEY_CATALYST, 10)),
                readNbtList(nbt.getList(KEY_OUTPUT, 10)));
    }

    public Recipe(List<ILabel> input, List<ILabel> catalyst, List<ILabel> output) {
        this(input.toArray(EMPTY_ARRAY), catalyst.toArray(EMPTY_ARRAY), output.toArray(EMPTY_ARRAY));
    }

    public Recipe(ILabel[] input, ILabel[] catalyst, ILabel[] output) {
        BiFunction<ILabel[], Integer, ILabel[]> convert = (ls, i) -> {
            ILabel[] ret = new ILabel[i];
            if (ls.length > i) throw new RuntimeException("Too many labels");
            System.arraycopy(ls, 0, ret, 0, ls.length);
            for (int j = ls.length; j < i; j++) ret[j] = ILabel.EMPTY;
            return ret;
        };
        this.input = convert.apply(input, 14);
        this.catalyst = convert.apply(catalyst, 7);
        this.output = convert.apply(output, 7);
        Stream.of(input, output).forEach(i -> Arrays.stream(i).filter(j -> j != ILabel.EMPTY).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid recipe")));
    }

    static private List<ILabel> readNbtList(ListNBT list) {
        return list.stream().filter(n -> n instanceof CompoundNBT)
                .map(n -> ILabel.SERIALIZER.deserialize((CompoundNBT) n))
                .collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        Wrapper<Integer> hash = new Wrapper<>(0);
        Consumer<ILabel[]> hasher = (ls) -> Arrays.stream(ls)
                .filter(Objects::nonNull).forEach(i -> hash.value ^= i.hashCode());
        hasher.accept(input);
        hasher.accept(catalyst);
        hasher.accept(output);
        return hash.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Recipe)) return false;
        Recipe r = (Recipe) obj;
        BiPredicate<ILabel[], ILabel[]> p = (i, j) -> {
            if (i.length != j.length) return false;
            for (int k = 0; k < i.length; k++)
                if (!i[k].equals(j[k])) return false;
            return true;
        };
        return p.test(input, r.input) && p.test(catalyst, r.catalyst) && p.test(output, r.output);
    }

    public ILabel[] getLabel(IO type) {
        switch (type) {
            case INPUT:
                return input;
            case OUTPUT:
                return output;
            case CATALYST:
                return catalyst;
            default:
                throw new RuntimeException("Internal error");
        }
    }

    public ILabel getRep() {
        for (int i = 0; i < 8; i++)
            if (output[i] != ILabel.EMPTY) return output[i];
        return ILabel.EMPTY;
    }

    public CompoundNBT serialize() {
        CompoundNBT ret = new CompoundNBT();
        Function<ILabel[], ListNBT> convert = (ls) -> {
            ArrayList<ILabel> labels = new ArrayList<>();
            boolean start = false;
            for (int i = ls.length - 1; i >= 0; i--) {
                if (start || ls[i] != ILabel.EMPTY) {
                    labels.add(ls[i]);
                    start = true;
                }
            }

            ListNBT r = new ListNBT();
            new Utilities.ReversedIterator<>(labels).stream()
                    .forEach(l -> r.add(ILabel.SERIALIZER.serialize(l)));
            return r;
        };
        ret.put(KEY_INPUT, convert.apply(input));
        ret.put(KEY_CATALYST, convert.apply(catalyst));
        ret.put(KEY_OUTPUT, convert.apply(output));
        return ret;
    }

    public Optional<ILabel> matches(ILabel label) {
        return Arrays.stream(output).filter(i -> ILabel.MERGER.merge(label, i).isPresent()).findAny();
    }

    public long multiplier(ILabel label) {
        return Arrays.stream(output).filter(i -> ILabel.MERGER.merge(label, i).isPresent()).findAny()
                .map(i -> {
                    long amountA = label.getAmount();
                    if (!label.isPercent()) amountA = Math.multiplyExact(amountA, 100L);
                    long amountB = i.getAmount();
                    if (!i.isPercent()) amountB = Math.multiplyExact(amountB, 100L);
                    return (amountB + Math.abs(amountA) - 1) / amountB;
                }).orElse(0L);
    }

    public enum IO {
        INPUT, OUTPUT, CATALYST;

        public int getSize() {
            return this == INPUT ? 16 : 8;
        }
    }
}
