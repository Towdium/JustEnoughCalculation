package me.towdium.jecalculation.data.label.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: towdium
 * Date:   17-9-10.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LItemTag extends ILabel.Impl {
    public static final String IDENTIFIER = "tag";
    public static final String KEY_NAME = "name";
    public static final boolean MODE_FORCE = false;

    protected ResourceLocation name;

    public LItemTag(ResourceLocation name) {
        this(name, 1);
    }

    public LItemTag(ResourceLocation name, long amount) {
        super(amount, false);
        this.name = name;
    }

    public LItemTag(LItemTag lod) {
        super(lod);
        this.name = lod.name;
    }

    public LItemTag(CompoundNBT nbt) {
        super(nbt);
        name = new ResourceLocation(nbt.getString(KEY_NAME));
    }

    public static boolean mergeSame(ILabel a, ILabel b) {
        if (a instanceof LItemTag && b instanceof LItemTag) {
            LItemTag lodA = (LItemTag) a;
            LItemTag lodB = (LItemTag) b;
            return lodA.getName().equals(lodB.getName());
        } else return false;
    }

    public static boolean mergeFuzzy(ILabel a, ILabel b) {
        if (a instanceof LItemTag && b instanceof LItemStack) {
            LItemTag lod = (LItemTag) a;
            LItemStack lis = (LItemStack) b;
            return lod.getAmount() * lis.getAmount() < 0
                    && discover(lod.name).map(Converter::from)
                    .anyMatch(i -> LItemStack.merge(i, lis));
        }
        return false;
    }

    private static Stream<ItemStack> discover(ResourceLocation tag) {
        Tag<Item> items = ItemTags.getCollection().get(tag);
        return items == null ? Stream.empty() : items.getAllElements().stream().map(ItemStack::new);
    }

    private static Collection<ResourceLocation> discover(ILabel label) {
        if (!(label instanceof LItemStack)) return Collections.emptyList();
        LItemStack lis = (LItemStack) label;
        return ItemTags.getCollection().getOwningTags(lis.item);
    }

    private static boolean matches(ResourceLocation tag, ILabel label) {
        if (!(label instanceof LItemStack)) return false;
        LItemStack lis = (LItemStack) label;
        Tag<Item> items = ItemTags.getCollection().get(tag);
        return items != null && items.contains(lis.item);
    }

    public static List<ILabel> suggest(List<ILabel> iss, @Nullable IRecipeLayout rl) {
        ILabel l = iss.get(0);
        if (!(l instanceof LItemStack)) return new ArrayList<>();
        LItemStack lis = (LItemStack) l;
        HashSet<ResourceLocation> ids = new HashSet<>();
        long amount = lis.getAmount();
        for (ResourceLocation i : discover(lis))
            if (check(i, iss, true)) ids.add(i);
        return ids.stream().map(i -> new LItemTag(i, amount))
                .collect(Collectors.toList());
    }

    public static List<ILabel> fallback(List<ILabel> iss, @Nullable IRecipeLayout rl) {
        ILabel l = iss.get(0);
        if (!(l instanceof LItemStack)) return new ArrayList<>();
        LItemStack lis = (LItemStack) l;
        HashSet<ResourceLocation> ids = new HashSet<>();
        long amount = lis.getAmount();
        for (ResourceLocation i : discover(lis))
            if (check(i, iss, false)) ids.add(i);
        return ids.stream().map(i -> new LItemTag(i, amount))
                .collect(Collectors.toList());
    }

    // check labels in the list suitable for the ore id
    private static boolean check(ResourceLocation id, List<ILabel> labels, boolean biDir) {
        Stream<ItemStack> ores = discover(id);
        Tag<Item> tag = ItemTags.getCollection().get(id);
        if (tag == null) return false;

        Wrapper<Boolean> acceptable = new Wrapper<>(true);
        BiPredicate<ILabel, ItemStack> match = (l, o) -> l instanceof LItemStack
                && o.equals(((LItemStack) l).getRep(), false);
        if (biDir) ores.filter(ore -> labels.stream().noneMatch(label -> match.test(label, ore)))
                .findAny().ifPresent(i -> acceptable.value = false);
        labels.stream().filter(label -> label instanceof LItemStack)
                .filter(label -> tag.contains(((LItemStack) label).item))
                .findAny().ifPresent(i -> acceptable.value = false);
        return acceptable.value;
    }

    @Override
    @Nonnull
    public ItemStack getRepresentation() {
        List<ItemStack> list = discover(name).collect(Collectors.toList());
        if (list.isEmpty()) return ItemStack.EMPTY;
        long index = System.currentTimeMillis() / 1500;
        return list.get((int) (index % list.size()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getDisplayName() {
        return Utilities.I18n.get("label.item_tag.name", name);
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean matches(Object l) {
        return l instanceof LItemTag && name.equals(((LItemTag) l).name) && super.matches(l);
    }

    @Override
    public LItemTag copy() {
        return new LItemTag(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawLabel(JecaGui gui) {
        gui.drawItemStack(0, 0, getRepresentation(), false);
        gui.drawResource(Resource.LBL_FRAME, 0, 0);
    }

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
