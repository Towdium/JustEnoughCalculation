package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
@ParametersAreNonnullByDefault
public class LItemStack extends ILabel.Impl {
    public static final String IDENTIFIER = "itemStack";
    public static final String KEY_ITEM = "item";
    public static final String KEY_META = "meta";
    public static final String KEY_NBT = "nbt";
    public static final String KEY_FUZZY = "fuzzy";
    public static final byte FUZZY_META = 0x1;
    public static final byte FUZZY_CAP = 0x2;
    public static final byte FUZZY_NBT = 0x4;

    Item item;
    int meta;
    NBTTagCompound nbt;
    boolean fMeta;
    boolean fNbt;
    boolean fCap;
    transient ItemStack temp;

    public LItemStack(int amount, Item item, int meta, @Nullable NBTTagCompound nbt, boolean percent) {
        super(amount, percent);
        this.item = item;
        this.meta = meta;
        this.nbt = nbt == null ? null : (NBTTagCompound) nbt.copy();
        temp = new ItemStack(item, 1, meta);
        temp.setTagCompound(nbt);
    }

    // Convert from itemStack
    public LItemStack(ItemStack is) {
        this(is.stackSize, is.getItem(), is.getItemDamage(), is.getTagCompound(), false);
    }

    public LItemStack(NBTTagCompound tag) {
        super(tag);
        item = Item.getItemById(tag.getInteger(KEY_ITEM));
        meta = tag.getInteger(KEY_META);
        nbt = tag.hasKey(KEY_NBT) ? tag.getCompoundTag(KEY_NBT) : null;
        byte fuzzy = tag.getByte(KEY_FUZZY);
        fMeta = (fuzzy & FUZZY_META) != 0;
        fCap = (fuzzy & FUZZY_CAP) != 0;
        fNbt = (fuzzy & FUZZY_NBT) != 0;
        temp = new ItemStack(item, 1, meta);
        temp.setTagCompound(tag);
    }

    private LItemStack(LItemStack lis) {
        super(lis);
        item = lis.item;
        meta = lis.meta;
        nbt = lis.nbt;
        fMeta = lis.fMeta;
        fNbt = lis.fNbt;
        fCap = lis.fCap;
        temp = new ItemStack(item, 1, meta);
        temp.setTagCompound(nbt);
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

    @Override
    public ItemStack getRepresentation() {
        return temp;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return temp.getItem() == null ? "" : temp.getDisplayName();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean matches(Object l) {
        if (l instanceof LItemStack) {
            LItemStack lis = (LItemStack) l;
            return (Objects.equals(nbt, lis.nbt)) && meta == lis.meta && item == lis.item
                   && fNbt == lis.fNbt && super.matches(l)
                   && fCap == lis.fCap && fMeta == lis.fMeta;
        } else
            return false;
    }

    @Override
    public LItemStack copy() {
        return new LItemStack(this);
    }

    @Override
    public NBTTagCompound toNBTTagCompound() {
        if (item == null)
            return ILabel.EMPTY.toNBTTagCompound();
        int id = Item.getIdFromItem(item);
        NBTTagCompound ret = super.toNBTTagCompound();
        ret.setInteger(KEY_META, meta);
        ret.setInteger(KEY_ITEM, id);
        if (nbt != null)
            ret.setTag(KEY_NBT, nbt);
        int fuzzy = (fMeta ? FUZZY_META : 0) | (fCap ? FUZZY_CAP : 0) | (fNbt ? FUZZY_NBT : 0);
        ret.setByte(KEY_FUZZY, (byte) fuzzy);
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
        return (nbt == null ? 0 : nbt.hashCode()) ^ meta ^ item.getUnlocalizedName().hashCode() ^ amount;
    }
}
