package me.towdium.jecalculation.data.label.labels;

import com.sun.istack.internal.NotNull;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LItemStack extends ILabel.Impl {
    public static final String IDENTIFIER = "itemStack";

    public static final String KEY_ITEM = "item";
    public static final String KEY_META = "meta";
    public static final String KEY_NBT = "nbt";
    public static final String KEY_CAP = "cap";
    public static final String KEY_F_META = "fMeta";
    public static final String KEY_F_CAP = "fCap";
    public static final String KEY_F_NBT = "fNbt";

    Item item;
    int meta;
    NBTTagCompound nbt;
    NBTTagCompound cap;
    boolean fMeta;
    boolean fNbt;
    boolean fCap;
    transient ItemStack temp;

    public LItemStack(int amount, Item item, int meta, @Nullable NBTTagCompound cap,
                      @Nullable NBTTagCompound nbt, boolean percent) {
        super(amount, percent);
        init(item, meta, cap, nbt, false, false, false);
    }

    // Convert from itemStack
    public LItemStack(ItemStack is) {
        super(is.getCount(), false);
        init(is.getItem(), is.getItemDamage(), getCap(is), is.getTagCompound(), false, false, false);
    }

    public LItemStack(NBTTagCompound tag) {
        super(tag);
        init(Item.getByNameOrId(tag.getString(KEY_ITEM)),
                tag.getInteger(KEY_META),
                tag.hasKey(KEY_CAP) ? tag.getCompoundTag(KEY_CAP) : null,
                tag.hasKey(KEY_NBT) ? tag.getCompoundTag(KEY_NBT) : null,
                tag.getBoolean(KEY_F_META),
                tag.getBoolean(KEY_F_CAP),
                tag.getBoolean(KEY_F_NBT)
        );
    }

    private LItemStack(LItemStack lis) {
        super(lis);
        item = lis.item;
        meta = lis.meta;
        nbt = lis.nbt;
        cap = lis.cap;
        fMeta = lis.fMeta;
        fNbt = lis.fNbt;
        fCap = lis.fCap;
        temp = lis.temp;
    }

    private void init(@Nullable Item item, int meta, @Nullable NBTTagCompound cap,
                      @Nullable NBTTagCompound nbt, boolean fMeta, boolean fCap, boolean fNbt) {
        Objects.requireNonNull(item);
        this.item = item;
        this.meta = meta;
        this.cap = cap;
        this.nbt = nbt;
        this.fMeta = fMeta;
        this.fCap = fCap;
        this.fNbt = fNbt;
        temp = new ItemStack(item, 1, meta, cap);
        temp.setTagCompound(nbt);
    }

    @Nullable
    private static NBTTagCompound getCap(ItemStack is) {
        NBTTagCompound nbt = is.serializeNBT();
        return nbt.hasKey("ForgeCaps") ? nbt.getCompoundTag("ForgeCaps") : null;
    }

    public static boolean merge(ILabel a, ILabel b) {
        if (a instanceof LItemStack && b instanceof LItemStack) {
            LItemStack lisA = (LItemStack) a;
            LItemStack lisB = (LItemStack) b;
            if (lisA.meta != WILDCARD_VALUE && lisB.meta == WILDCARD_VALUE) return false;
            if (!lisA.fNbt) {
                if (lisB.fNbt) return false;
                else if (lisA.nbt == null) {
                    if (lisB.nbt != null) return false;
                } else if (lisB.nbt == null || !lisA.nbt.equals(lisB.nbt)) return false;
            }
            if (!lisA.fCap) {
                if (lisB.fCap) return false;
                else if (lisA.cap == null) {
                    if (lisB.cap != null) return false;
                } else if (lisB.cap == null || !lisA.cap.equals(lisB.cap)) return false;
            }
            return lisA.item == lisB.item;
        }
        return false;
    }

    public ILabel setFMeta(boolean f) {
        fMeta = f;
        return this;
    }

    public ILabel setFNbt(boolean f) {
        fNbt = f;
        return this;
    }

    public ILabel setFCap(boolean f) {
        fCap = f;
        return this;
    }

    public ItemStack getRep() {
        return temp;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(item));
    }

    @NotNull
    @Override
    public ItemStack getRepresentation() {
        return temp;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return temp.getDisplayName();
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
                    && meta == lis.meta && item == lis.item
                    && fNbt == lis.fNbt && super.matches(l)
                    && fCap == lis.fCap && fMeta == lis.fMeta;
        } else return false;
    }

    @Override
    public LItemStack copy() {
        return new LItemStack(this);
    }

    @Override
    public NBTTagCompound toNbt() {
        ResourceLocation rl = Item.REGISTRY.getNameForObject(item);
        if (rl == null) return ILabel.EMPTY.toNbt();
        NBTTagCompound ret = super.toNbt();
        if (meta != 0) ret.setInteger(KEY_META, meta);
        ret.setString(KEY_ITEM, rl.toString());
        if (nbt != null) ret.setTag(KEY_NBT, nbt);
        if (cap != null) ret.setTag(KEY_CAP, cap);
        if (fMeta) ret.setBoolean(KEY_F_META, true);
        if (fNbt) ret.setBoolean(KEY_F_NBT, true);
        if (fCap) ret.setBoolean(KEY_F_CAP, true);
        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
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
                ^ meta ^ item.getTranslationKey().hashCode() ^ amount;
    }
}
