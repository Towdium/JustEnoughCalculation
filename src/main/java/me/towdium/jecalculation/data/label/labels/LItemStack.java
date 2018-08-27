package me.towdium.jecalculation.data.label.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    Item item;
    int meta;
    NBTTagCompound nbt;
    NBTTagCompound cap;
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
            boolean wildcard = lisA.meta == OreDictionary.WILDCARD_VALUE
                    || lisB.meta == OreDictionary.WILDCARD_VALUE;
            if ((lisA.cap == null ? lisB.cap == null : lisA.cap.equals(lisB.cap))
                    && (lisA.nbt == null ? lisB.nbt == null : lisA.nbt.equals(lisB.nbt))
                    && (lisA.meta == lisB.meta || wildcard)) {
                LItemStack ret = new LItemStack(lisA);
                if (wildcard) ret.meta = OreDictionary.WILDCARD_VALUE;
                ret.amount = add ? lisA.amount + lisB.amount : lisA.amount - lisB.amount;
                return Optional.of(ret.amount == 0 ? ILabel.EMPTY : ret);
            }
        }
        return Optional.empty();
    }

    public ItemStack getRep() {
        return temp;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(item));
        return existing;
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
                    && meta == lis.meta && item == lis.item;
        } else return false;
    }

    @Override
    public ILabel copy() {
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
        if (nbt != null) ret.setTag(KEY_CAP, cap);
        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecaGui gui) {
        gui.drawItemStack(0, 0, temp, false);
    }

    @Override
    public int hashCode() {
        return (nbt == null ? 0 : nbt.hashCode()) ^ (cap == null ? 0 : cap.hashCode())
                ^ meta ^ item.getUnlocalizedName().hashCode() ^ amount;
    }
}
