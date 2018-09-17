package me.towdium.jecalculation.data.label.labels;

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
import java.util.Optional;

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
    public static final String KEY_FUZZY = "fuzzy";
    public static final byte FUZZY_META = 0x1;
    public static final byte FUZZY_CAP = 0x2;
    public static final byte FUZZY_NBT = 0x4;

    Item item;
    int meta;
    NBTTagCompound nbt;
    NBTTagCompound cap;
    boolean fMeta;
    boolean fNbt;
    boolean fCap;
    transient ItemStack temp;

    public LItemStack(int amount, Item item, int meta,
                      @Nullable NBTTagCompound cap, @Nullable NBTTagCompound nbt) {
        super(amount);
        this.item = item;
        this.meta = meta;
        this.nbt = nbt == null ? null : nbt.copy();
        this.cap = cap == null ? null : cap.copy();
        temp = new ItemStack(item, 1, meta, cap);
        temp.setTagCompound(nbt);
    }

    // Convert from itemStack
    public LItemStack(ItemStack is) {
        this(is.getCount(), is.getItem(), is.getItemDamage(), getCap(is), is.getTagCompound());
    }

    public LItemStack(NBTTagCompound nbt) {
        this(nbt.getInteger(KEY_AMOUNT), Objects.requireNonNull(Item.getByNameOrId(nbt.getString(KEY_ITEM))),
                nbt.getInteger(KEY_META), nbt.hasKey(KEY_CAP) ? nbt.getCompoundTag(KEY_CAP) : null,
                nbt.hasKey(KEY_NBT) ? nbt.getCompoundTag(KEY_NBT) : null);
        fromFuzzy(nbt.getByte(KEY_FUZZY));
    }

    private LItemStack(LItemStack lis) {
        super(lis);
        item = lis.item;
        meta = lis.meta;
        nbt = lis.nbt;
        cap = lis.cap;
        temp = new ItemStack(item, 1, meta, cap);
        temp.setTagCompound(nbt);
    }

    @Nullable
    private static NBTTagCompound getCap(ItemStack is) {
        NBTTagCompound nbt = is.serializeNBT();
        return nbt.hasKey("ForgeCaps") ? nbt.getCompoundTag("ForgeCaps") : null;
    }

    public static Optional<ILabel> merge(ILabel a, ILabel b, boolean add) {
        if (a instanceof LItemStack && b instanceof LItemStack) {
            LItemStack lisA = (LItemStack) a;
            LItemStack lisB = (LItemStack) b;

            // check meta
            int resultMeta = 0;
            boolean checkedMeta = true;
            if (lisA.meta == WILDCARD_VALUE || lisB.meta == WILDCARD_VALUE) resultMeta = WILDCARD_VALUE;
            else if (lisA.fMeta || lisB.fMeta || lisA.meta == lisB.meta) resultMeta = lisA.meta;
            else checkedMeta = false;

            // check nbt and cap
            boolean checkedNbt, checkedCap;
            checkedNbt = (lisA.nbt == null ? lisB.nbt == null : lisA.nbt.equals(lisB.nbt))
                    || lisA.fNbt || lisB.fNbt;
            checkedCap = (lisA.cap == null ? lisB.cap == null : lisA.cap.equals(lisB.cap))
                    || lisA.fCap || lisB.fCap;

            if (lisA.item == lisB.item && checkedCap && checkedMeta && checkedNbt) {
                LItemStack is = lisA.copy();
                is.meta = resultMeta;
                return Impl.merge(is, lisB, add);
            }
        }
        return Optional.empty();
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
    public NBTTagCompound toNBTTagCompound() {
        ResourceLocation rl = Item.REGISTRY.getNameForObject(item);
        if (rl == null) return ILabel.EMPTY.toNBTTagCompound();
        NBTTagCompound ret = super.toNBTTagCompound();
        ret.setInteger(KEY_META, meta);
        ret.setString(KEY_ITEM, rl.toString());
        if (nbt != null) ret.setTag(KEY_NBT, nbt);
        if (cap != null) ret.setTag(KEY_CAP, cap);
        ret.setByte(KEY_FUZZY, toFuzzy());
        return ret;
    }

    private byte toFuzzy() {
        return (byte) ((fMeta ? FUZZY_META : 0) | (fCap ? FUZZY_CAP : 0) | (fNbt ? FUZZY_NBT : 0));
    }

    private void fromFuzzy(byte i) {
        fMeta = (i & FUZZY_META) != 0;
        fCap = (i & FUZZY_CAP) != 0;
        fNbt = (i & FUZZY_NBT) != 0;
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
