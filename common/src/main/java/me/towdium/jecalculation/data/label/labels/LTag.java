package me.towdium.jecalculation.data.label.labels;

import com.mojang.datafixers.util.Pair;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class LTag<T> extends LContext<T> {
    public static final String KEY_NAME = "name";

    protected TagKey<T> name;

    public LTag(TagKey<T> name) {
        this(name, 1);
    }

    public LTag(TagKey<T> name, long amount) {
        super(amount, false);
        this.name = name;
    }

    public LTag(LTag<T> lt) {
        super(lt);
        this.name = lt.name;
    }

    public LTag(CompoundTag nbt) {
        super(nbt);
        name = TagKey.create(getRegistry().key(), new ResourceLocation(nbt.getString(KEY_NAME)));
    }

    protected abstract Registry<T> getRegistry();

    public static boolean mergeSame(ILabel a, ILabel b) {
        if (a instanceof LTag<?> lodA && b instanceof LTag<?> lodB) {
            return lodA.getName().equals(lodB.getName())
                    && lodA.getContext() == lodB.getContext();
        } else return false;
    }

    public static boolean mergeFuzzy(ILabel a, ILabel b) {
        if (a instanceof LTag<?> lt && b instanceof LStack<?> ls) {
            return lt.getAmount() * ls.getAmount() < 0
                    && lt.getContext().matches(lt.name, ls);
        }
        return false;
    }

    public static List<ILabel> suggest(List<ILabel> is, @Nullable Class<?> context) {
        return convert(is, true);
    }

    private static <T> List<ILabel> convert(List<ILabel> is, boolean biDir) {
        @SuppressWarnings("unchecked") List<LStack<T>> iss = is.stream().filter(i -> i instanceof LStack)
                .map(i -> (LStack<T>) i).collect(Collectors.toList());
        if (iss.isEmpty() || iss.size() != is.size()) return Collections.emptyList();
        LStack<T> lis = iss.get(0);
        if (iss.stream().anyMatch(i -> i.getContext() != iss.get(0).getContext())) return Collections.emptyList();
        HashSet<TagKey<T>> ids = new HashSet<>();
        long amount = lis.getAmount();
        for (TagKey<T> i : lis.getContext().discover(lis))
            if (check(i, iss, biDir)) ids.add(i);
        return ids.stream().map(i -> lis.getContext().create(i, amount))
                .collect(Collectors.toList());
    }

    public static List<ILabel> fallback(List<ILabel> is, @Nullable Class<?> context) {
        return convert(is, false);
    }

    // check labels in the list suitable for the ore id
    private static <T> boolean check(TagKey<T> id, List<LStack<T>> labels, boolean biDir) {
        if (!id.isFor(Registry.ITEM_REGISTRY))
            return false;
        Stream<LStack<T>> ores = labels.get(0).getContext().discover(id);
        Optional<List<Item>> tag = Registry.ITEM.getTags()
                .filter(pair -> pair.getFirst().equals(id))
                .map(Pair::getSecond)
                .findFirst()
                .map(HolderSet.Named::stream)
                .map(s -> s.map(Holder::value))
                .map(Stream::toList);
        if (tag.isEmpty()) return false;

        Wrapper<Boolean> acceptable = new Wrapper<>(true);
        if (biDir) ores.filter(ore -> labels.stream().noneMatch(ore::matches))
                .findAny().ifPresent(i -> acceptable.value = false);
        labels.stream().filter(label -> label instanceof LItemStack)
                .filter(label -> !tag.get().contains(((LItemStack) label).item))
                .findAny().ifPresent(i -> acceptable.value = false);
        return acceptable.value;
    }

    @Override
    public Object getRepresentation() {
        List<LStack<T>> list = getContext().discover(name).toList();
        if (list.isEmpty()) return ItemStack.EMPTY;
        long index = System.currentTimeMillis() / 1500;
        return list.get((int) (index % list.size())).getRepresentation();
    }

    @Override
    public boolean matches(Object l) {
        if (!(l instanceof LTag<?> tag)) return false;
        return tag.getContext() == getContext() && tag.name.equals(name) && super.matches(l);
    }

    @Override
    public abstract LTag<T> copy();

    @Override
    public CompoundTag toNbt() {
        CompoundTag ret = super.toNbt();
        ret.putString(KEY_NAME, name.location().toString());
        return ret;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + JustEnoughCalculation.MODNAME);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ super.hashCode();
    }

    public String getName() {
        return name.location().toString();
    }
}
