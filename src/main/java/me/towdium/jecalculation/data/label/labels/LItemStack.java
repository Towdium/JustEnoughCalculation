package me.towdium.jecalculation.data.label.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.ILabel.Serializer.SerializationException;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LItemStack extends LStack<Item> {  // TODO fix item damage
    public static final String IDENTIFIER = "itemStack";

    public static final String KEY_ITEM = "item";
    public static final String KEY_META = "meta";
    public static final String KEY_NBT = "nbt";
    public static final String KEY_CAP = "cap";
    public static final String KEY_F_META = "fMeta";
    public static final String KEY_F_CAP = "fCap";
    public static final String KEY_F_NBT = "fNbt";

    Item item;
    CompoundNBT nbt;
    CompoundNBT cap;
    boolean fMeta;
    boolean fNbt;
    boolean fCap;
    transient ItemStack temp;

    // Convert from itemStack
    public LItemStack(ItemStack is) {
        super(is.getCount(), false);
        init(is.getItem(), getCap(is), is.getTag(), false, false, false);
    }

    public LItemStack(CompoundNBT tag) {
        super(tag);
        String id = tag.getString(KEY_ITEM);
        Item i = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (i == null) throw new SerializationException("Item " + id + " cannot be resolved, ignoring");
        init(i, tag.contains(KEY_CAP) ? tag.getCompound(KEY_CAP) : null,
                tag.contains(KEY_NBT) ? tag.getCompound(KEY_NBT) : null,
                tag.getBoolean(KEY_F_META),
                tag.getBoolean(KEY_F_CAP),
                tag.getBoolean(KEY_F_NBT)
        );
    }

    @Override
    public Item get() {
        return item;
    }

    @Override
    public Context<Item> getContext() {
        return Context.ITEM;
    }

    private LItemStack(LItemStack lis) {
        super(lis);
        item = lis.item;
        nbt = lis.nbt;
        cap = lis.cap;
        fMeta = lis.fMeta;
        fNbt = lis.fNbt;
        fCap = lis.fCap;
        temp = lis.temp;
    }

    private void init(@Nullable Item item, @Nullable CompoundNBT cap,
                      @Nullable CompoundNBT nbt, boolean fMeta, boolean fCap, boolean fNbt) {
        Objects.requireNonNull(item);
        this.item = item;
        this.cap = fCap ? null : cap;
        this.nbt = fNbt ? null : nbt;
        this.fMeta = fMeta;
        this.fCap = fCap;
        this.fNbt = fNbt;
        temp = new ItemStack(item, 1, this.cap);
        temp.setTag(this.nbt);
    }

    @Nullable
    private static CompoundNBT getCap(ItemStack is) {
        CompoundNBT nbt = is.serializeNBT();
        return nbt.contains("ForgeCaps") ? nbt.getCompound("ForgeCaps") : null;
    }

    public static boolean merge(ILabel a, ILabel b) {
        if (a instanceof LItemStack && b instanceof LItemStack) {
            LItemStack lisA = (LItemStack) a;
            LItemStack lisB = (LItemStack) b;

//            if (lisA.meta != lisB.meta && !lisA.fMeta
//                    && !lisB.fMeta && lisB.meta != WILDCARD_VALUE) return false;
            if (!lisA.fNbt && !lisB.fNbt) {
                if (lisA.nbt == null) {
                    if (lisB.nbt != null) return false;
                } else if (lisB.nbt == null || !lisA.nbt.equals(lisB.nbt)) return false;
            }
            if (!lisA.fCap && !lisB.fCap) {
                if (lisA.cap == null) {
                    if (lisB.cap != null) return false;
                } else if (lisB.cap == null || !lisA.cap.equals(lisB.cap)) return false;
            }
            return lisA.item == lisB.item;
        }
        return false;
    }

    public static List<ILabel> suggest(List<ILabel> iss, @Nullable IRecipeLayout rl) {
        if (iss.size() == 0) return new ArrayList<>();
        for (ILabel i : iss) if (!(i instanceof LItemStack)) return new ArrayList<>();
        LItemStack lis = (LItemStack) iss.get(0);
        boolean fMeta = false;
        boolean fNbt = false;
        boolean fCap = false;
        for (ILabel i : iss) {
            LItemStack ii = (LItemStack) i;
            if (ii.item != lis.item) return new ArrayList<>();
            //if (ii.meta != lis.meta || ii.fMeta) fMeta = true;
            if (ii.nbt == null ? lis.nbt != null : !ii.nbt.equals(lis.nbt)) fNbt = true;
            if (ii.cap == null ? lis.cap != null : !ii.cap.equals(lis.cap)) fCap = true;
        }
        if (fMeta || fNbt || fCap) return Collections.singletonList(
                lis.copy().setFCap(fCap).setFMeta(fMeta).setFNbt(fNbt));
        else return new ArrayList<>();
    }

    public static List<ILabel> fallback(List<ILabel> iss, @Nullable IRecipeLayout rl) {
        List<ILabel> ret = new ArrayList<>();
        if (iss.size() == 1) {
            ILabel label = iss.get(0);
            if (!(label instanceof LItemStack)) return ret;
            LItemStack lis = (LItemStack) label;
            if (lis.fCap || lis.fNbt || lis.fMeta) return new ArrayList<>();
            ret.add(lis.copy().setFMeta(true));
            ret.add(lis.copy().setFNbt(true));
            ret.add(lis.copy().setFCap(true));
            ret.add(lis.copy().setFMeta(true).setFNbt(true).setFCap(true));
        }
        return ret;
    }

    public LItemStack setFMeta(boolean f) {
        fMeta = f;
        //if (f) meta = 0;
        temp = new ItemStack(item, 1, cap);
        temp.setTag(nbt);
        return this;
    }

    public LItemStack setFNbt(boolean f) {
        fNbt = f;
        if (f) nbt = null;
        temp = new ItemStack(item, 1, cap);
        temp.setTag(nbt);
        return this;
    }

    public LItemStack setFCap(boolean f) {
        fCap = f;
        if (f) cap = null;
        temp = new ItemStack(item, 1, cap);
        temp.setTag(nbt);
        return this;
    }

    public ItemStack getRep() {
        return temp;
    }

    @Override
    public void getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        if (fMeta) existing.add(FORMAT_GREY + Utilities.I18n.get("label.item_stack.fuzzy_meta"));
        if (fNbt) existing.add(FORMAT_GREY + Utilities.I18n.get("label.item_stack.fuzzy_nbt"));
        if (fCap) existing.add(FORMAT_GREY + Utilities.I18n.get("label.item_stack.fuzzy_cap"));
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(item));
    }

    @Override
    public ItemStack getRepresentation() {
        return temp;
    }

    @Override
    public String getDisplayName() {
        return temp.getDisplayName().getFormattedText();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean matches(Object l) {
        if (l instanceof LItemStack) {
            LItemStack lis = (LItemStack) l;
            return (nbt == null ? lis.nbt == null : nbt.equals(lis.nbt))
                    && (cap == null ? lis.cap == null : cap.equals(lis.cap))
                    //&& meta == lis.meta
                    && item == lis.item
                    && fNbt == lis.fNbt && super.matches(l)
                    && fCap == lis.fCap && fMeta == lis.fMeta;
        } else return false;
    }

    @Override
    public LItemStack copy() {
        return new LItemStack(this);
    }

    @Override
    public CompoundNBT toNbt() {
        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
        if (rl == null) return ILabel.EMPTY.toNbt();
        CompoundNBT ret = super.toNbt();
        //if (meta != 0) ret.putInt(KEY_META, meta);
        ret.putString(KEY_ITEM, rl.toString());
        if (nbt != null) ret.put(KEY_NBT, nbt);
        if (cap != null) ret.put(KEY_CAP, cap);
        if (fMeta) ret.putBoolean(KEY_F_META, true);
        if (fNbt) ret.putBoolean(KEY_F_NBT, true);
        if (fCap) ret.putBoolean(KEY_F_CAP, true);
        return ret;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawLabel(JecaGui gui) {
        gui.drawItemStack(0, 0, temp, false);
        if (fCap || fNbt || fMeta) gui.drawResource(Resource.LBL_FRAME, 0, 0);
        if (fCap) gui.drawResource(Resource.LBL_FR_LL, 0, 0);
        if (fNbt) gui.drawResource(Resource.LBL_FR_UL, 0, 0);
        if (fMeta) gui.drawResource(Resource.LBL_FR_UR, 0, 0);
    }

    @Override
    public int hashCode() {
        return (nbt == null ? 0 : nbt.hashCode()) ^ (cap == null ? 0 : cap.hashCode())
                //^ meta
                ^ item.getTranslationKey().hashCode() ^ super.hashCode();
    }
}
