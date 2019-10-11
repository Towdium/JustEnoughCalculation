package me.towdium.jecalculation.data.label.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class LTag<T> extends LContext<T> {
    //public static final String IDENTIFIER = "tag";
    public static final String KEY_NAME = "name";

    protected ResourceLocation name;

    public LTag(ResourceLocation name) {
        this(name, 1);
    }

    public LTag(ResourceLocation name, long amount) {
        super(amount, false);
        this.name = name;
    }

    public LTag(LTag<T> lt) {
        super(lt);
        this.name = lt.name;
    }

    public LTag(CompoundNBT nbt) {
        super(nbt);
        name = new ResourceLocation(nbt.getString(KEY_NAME));
    }

    public static boolean mergeSame(ILabel a, ILabel b) {
        if (a instanceof LTag && b instanceof LTag) {
            LTag lodA = (LTag) a;
            LTag lodB = (LTag) b;
            return lodA.getName().equals(lodB.getName())
                    && lodA.getContext() == lodB.getContext();
        } else return false;
    }

    public static boolean mergeFuzzy(ILabel a, ILabel b) {
        if (a instanceof LTag && b instanceof LStack) {
            LTag<?> lt = (LTag) a;
            LStack ls = (LStack) b;
            return lt.getContext() == ls.getContext()
                    && lt.getAmount() * ls.getAmount() < 0
                    && lt.getContext().discover(lt.name).map(Converter::from)
                    .anyMatch(i -> MERGER.merge(i, ls).isPresent());
        }
        return false;
    }

    private static <T> boolean matches(ResourceLocation tag, ILabel label) {
        if (!(label instanceof LStack)) return false;
        @SuppressWarnings("unchecked") LStack<T> ls = (LStack<T>) label;
        Tag<T> tags = ls.getContext().tags().get(tag);
        return tags != null && tags.contains(ls.get());
    }

    public static List<ILabel> suggest(List<ILabel> is, @Nullable IRecipeLayout rl) {
        return convert(is, true);
    }

    private static <T> List<ILabel> convert(List<ILabel> is, boolean biDir) {
        @SuppressWarnings("unchecked") List<LStack<T>> iss = is.stream().filter(i -> i instanceof LStack)
                .map(i -> (LStack<T>) i).collect(Collectors.toList());
        if (iss.isEmpty() || iss.size() != is.size()) return Collections.emptyList();
        LStack<T> lis = iss.get(0);
        if (iss.stream().anyMatch(i -> i.getContext() != iss.get(0).getContext())) return Collections.emptyList();
        HashSet<ResourceLocation> ids = new HashSet<>();
        long amount = lis.getAmount();
        for (ResourceLocation i : lis.getContext().discover(lis))
            if (check(i, iss, biDir)) ids.add(i);
        return ids.stream().map(i -> lis.getContext().create(i, amount))
                .collect(Collectors.toList());
    }

    public static List<ILabel> fallback(List<ILabel> is, @Nullable IRecipeLayout rl) {
        return convert(is, false);
    }

    // check labels in the list suitable for the ore id
    private static <T> boolean check(ResourceLocation id, List<LStack<T>> labels, boolean biDir) {
        Stream<LStack<T>> ores = labels.get(0).getContext().discover(id);
        Tag<Item> tag = ItemTags.getCollection().get(id);
        if (tag == null) return false;

        Wrapper<Boolean> acceptable = new Wrapper<>(true);
        if (biDir) ores.filter(ore -> labels.stream().noneMatch(ore::matches))
                .findAny().ifPresent(i -> acceptable.value = false);
        labels.stream().filter(label -> label instanceof LItemStack)
                .filter(label -> tag.contains(((LItemStack) label).item))
                .findAny().ifPresent(i -> acceptable.value = false);
        return acceptable.value;
    }

    @Override
    public Object getRepresentation() {
        List<LStack<T>> list = getContext().discover(name).collect(Collectors.toList());
        if (list.isEmpty()) return ItemStack.EMPTY;
        long index = System.currentTimeMillis() / 1500;
        return list.get((int) (index % list.size())).getRepresentation();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getDisplayName() {
        return Utilities.I18n.get("label.item_tag.name", name);
    }

    @Override
    public boolean matches(Object l) {
        if (!(l instanceof LTag)) return false;
        LTag<?> tag = (LTag<?>) l;
        return tag.getContext() == getContext() && tag.name.equals(name) && super.matches(l);
    }

    @Override
    public abstract LTag copy();

    @Override
    public CompoundNBT toNbt() {
        CompoundNBT ret = super.toNbt();
        ret.putString(KEY_NAME, name.toString());
        return ret;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + JustEnoughCalculation.MODNAME);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ super.hashCode();
    }

    public String getName() {
        return name.toString();
    }
}
