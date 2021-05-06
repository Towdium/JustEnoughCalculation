package me.towdium.jecalculation.data.label.labels;

import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
@ParametersAreNonnullByDefault
public class LItemStack extends ILabel.Impl {
    public static final String IDENTIFIER = "itemStack";

    private static final String KEY_ITEM = "item";
    private static final String KEY_META = "meta";
    private static final String KEY_NBT = "tag"; // don't modify it
    private static final String KEY_F_META = "fMeta";
    private static final String KEY_F_NBT = "fNbt";

    Item item;
    int meta;
    NBTTagCompound nbt;
    boolean fMeta;
    boolean fNbt;
    transient ItemStack rep;

    // Convert from itemStack
    public LItemStack(ItemStack is) {
        super(is.stackSize, false);
        init(is.getItem(), is.getItemDamage(), is.getTagCompound(), false, false);
    }

    public LItemStack(NBTTagCompound tag) {
        super(tag);
        final String strId = tag.getString(KEY_ITEM);
        if (!tag.hasKey("id"))
            tag.setShort("id", (short) GameData.getItemRegistry().getId(strId));
        tag.setByte("Count", (byte) 1);
        tag.setShort("Damage", tag.hasKey(KEY_META) ? (short) tag.getInteger(KEY_META) : 0);
        ItemStack stack = ItemStack.loadItemStackFromNBT(tag);

        if (ItemStackHelper.isEmpty(stack))
            throw new Serializer.SerializationException("Item " + strId + " cannot be resolved, ignoring");
        Item i = stack.getItem();
        init(i, tag.getInteger(KEY_META), tag.hasKey(KEY_NBT) ? stack.stackTagCompound : null,
             tag.getBoolean(KEY_F_META), tag.getBoolean(KEY_F_NBT));
    }

    private LItemStack(LItemStack lis) {
        super(lis);
        item = lis.item;
        meta = lis.meta;
        nbt = lis.nbt;
        fMeta = lis.fMeta;
        fNbt = lis.fNbt;
        rep = lis.rep;
    }

    private void init(@Nullable Item item, int meta, @Nullable NBTTagCompound nbt, boolean fMeta, boolean fNbt) {
        Objects.requireNonNull(item);
        this.item = item;
        this.meta = fMeta ? 0 : meta;
        this.nbt = fNbt ? null : nbt;
        this.fMeta = fMeta;
        this.fNbt = fNbt;
        rep = new ItemStack(item, 1, this.meta);
        rep.setTagCompound(this.nbt);
    }


    public static boolean merge(ILabel a, ILabel b) {
        if (a instanceof LItemStack && b instanceof LItemStack) {
            LItemStack lisA = (LItemStack) a;
            LItemStack lisB = (LItemStack) b;
            if (lisA.meta != lisB.meta && !lisA.fMeta && lisA.meta != WILDCARD_VALUE && !lisB.fMeta &&
                lisB.meta != WILDCARD_VALUE)
                return false;
            if (!lisA.fNbt && !lisB.fNbt) {
                if (lisA.nbt == null) {
                    if (lisB.nbt != null)
                        return false;
                } else if (lisB.nbt == null || !lisA.nbt.equals(lisB.nbt))
                    return false;
            }
            return lisA.item == lisB.item;
        }
        return false;
    }

    public static List<ILabel> suggest(List<ILabel> iss, @Nullable IRecipeHandler rl) {
        if (iss.size() == 0)
            return new ArrayList<>();
        for (ILabel i : iss)
            if (!(i instanceof LItemStack))
                return new ArrayList<>();
        LItemStack lis = (LItemStack) iss.get(0);
        boolean fMeta = false;
        boolean fNbt = false;
        for (ILabel i : iss) {
            LItemStack ii = (LItemStack) i;
            if (ii.item != lis.item)
                return new ArrayList<>();
            if (ii.meta != lis.meta || ii.fMeta)
                fMeta = true;
            if (!Objects.equals(ii.nbt, lis.nbt))
                fNbt = true;
        }
        if (fMeta || fNbt)
            return Collections.singletonList(lis.copy().setFMeta(fMeta).setFNbt(fNbt));
        else
            return new ArrayList<>();
    }


    public static List<ILabel> fallback(List<ILabel> iss, @Nullable IRecipeHandler rl) {
        List<ILabel> ret = new ArrayList<>();
        if (iss.size() == 1) {
            ILabel label = iss.get(0);
            if (!(label instanceof LItemStack))
                return ret;
            LItemStack lis = (LItemStack) label;
            if (lis.fNbt || lis.fMeta)
                return new ArrayList<>();
            ret.add(lis.copy().setFMeta(true));
            ret.add(lis.copy().setFNbt(true));
            ret.add(lis.copy().setFMeta(true).setFNbt(true));
        }
        return ret;
    }

    public LItemStack setFMeta(boolean f) {
        fMeta = f;
        if (f)
            meta = 0;
        rep = new ItemStack(item, 1, meta);
        rep.setTagCompound(nbt);
        return this;
    }

    public LItemStack setFNbt(boolean f) {
        fNbt = f;
        if (f)
            nbt = null;
        rep = new ItemStack(item, 1, meta);
        rep.setTagCompound(nbt);
        return this;
    }

    public ItemStack getRep() {
        return rep;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        if (fMeta)
            existing.add(FORMAT_GREY + Utilities.I18n.get("label.item_stack.fuzzy_meta"));
        if (fNbt)
            existing.add(FORMAT_GREY + Utilities.I18n.get("label.item_stack.fuzzy_nbt"));
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(item));
    }

    @Override
    public ItemStack getRepresentation() {
        return rep;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return rep.getItem() == null ? "" : rep.getDisplayName();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean matches(Object l) {
        if (l instanceof LItemStack) {
            LItemStack lis = (LItemStack) l;
            return (Objects.equals(nbt, lis.nbt)) && meta == lis.meta && item == lis.item && fNbt == lis.fNbt &&
                   super.matches(l) && fMeta == lis.fMeta;
        } else
            return false;
    }

    @Override
    public LItemStack copy() {
        return new LItemStack(this);
    }

    @Override
    public NBTTagCompound toNbt() {
        if (item == null)
            return ILabel.EMPTY.toNbt();
        String strId = Item.itemRegistry.getNameForObject(item);
        NBTTagCompound ret = super.toNbt();
        if (meta != 0)
            ret.setInteger(KEY_META, meta);
        ret.setString(KEY_ITEM, strId);
        if (nbt != null)
            ret.setTag(KEY_NBT, nbt);
        if (fMeta)
            ret.setBoolean(KEY_F_META, true);
        if (fNbt)
            ret.setBoolean(KEY_F_NBT, true);
        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecaGui gui) {
        gui.drawItemStack(0, 0, rep, false);
        // TODO some item won't render correctly with overlay
        //        if (fNbt || fMeta)
        //            gui.drawResource(Resource.LBL_FRAME, 0, 0);
        //        if (fNbt)
        //            gui.drawResource(Resource.LBL_FR_UL, 0, 0);
        //        if (fMeta)
        //            gui.drawResource(Resource.LBL_FR_UR, 0, 0);
    }

    @Override
    public int hashCode() { // TODO all labels use super hashcode
        return (nbt == null ? 0 : nbt.hashCode()) ^ meta ^ item.getUnlocalizedName().hashCode() ^ (int) amount;
    }
}
