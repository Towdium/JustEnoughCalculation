package me.towdium.jecalculation.data.label.labels;

import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.polyfill.mc.util.NonNullList;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-10.
 */
@ParametersAreNonnullByDefault
public class LOreDict extends ILabel.Impl {
    public static final String IDENTIFIER = "oreDict";
    public static final String KEY_NAME = "name";
    public static final boolean MODE_FORCE = false;

    protected String name;

    public LOreDict(String name) {
        this(name, 1);
    }

    public LOreDict(String name, long amount) {
        super(amount, false);
        this.name = name;
    }

    public LOreDict(LOreDict lod) {
        super(lod);
        this.name = lod.name;
    }

    public LOreDict(NBTTagCompound nbt) {
        super(nbt);
        name = nbt.getString(KEY_NAME);
    }

    public static boolean mergeSame(ILabel a, ILabel b) {
        if (a instanceof LOreDict && b instanceof LOreDict) {
            LOreDict lodA = (LOreDict) a;
            LOreDict lodB = (LOreDict) b;
            return lodA.getName().equals(lodB.getName());
        } else
            return false;
    }

    public static boolean mergeFuzzy(ILabel a, ILabel b) {
        if (a instanceof LOreDict && b instanceof LItemStack) {
            LOreDict lod = (LOreDict) a;
            LItemStack lis = (LItemStack) b;
            if (lod.getAmount() * lis.getAmount() < 0) {
                for (ItemStack ore : OreDictionary.getOres(lod.name)) {
                    if (LItemStack.merge(Converter.from(ore), lis)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<ILabel> suggest(List<ILabel> iss, @Nullable IRecipeHandler rl) {
        ILabel l = iss.get(0);
        if (!(l instanceof LItemStack))
            return new ArrayList<>();
        LItemStack lis = (LItemStack) l;
        HashSet<Integer> ids = new HashSet<>();
        long amount = lis.getAmount();
        for (int i : OreDictionary.getOreIDs(lis.getRep()))
            if (check(i, iss, true))
                ids.add(i);
        return ids.stream().map(i -> new LOreDict(OreDictionary.getOreName(i), amount)).collect(Collectors.toList());
    }

    public static List<ILabel> fallback(List<ILabel> iss, @Nullable IRecipeHandler rl) {
        ILabel l = iss.get(0);
        if (!(l instanceof LItemStack))
            return new ArrayList<>();
        LItemStack lis = (LItemStack) l;
        HashSet<Integer> ids = new HashSet<>();
        long amount = lis.getAmount();
        for (int i : OreDictionary.getOreIDs(lis.getRep()))
            if (check(i, iss, false))
                ids.add(i);
        return ids.stream().map(i -> new LOreDict(OreDictionary.getOreName(i), amount)).collect(Collectors.toList());
    }

    // check labels in the list suitable for the ore id
    private static boolean check(int id, List<ILabel> labels, boolean biDir) {
        ArrayList<ItemStack> ores = OreDictionary.getOres(OreDictionary.getOreName(id));
        if (labels.size() == 1 && ores.size() == 1 && biDir && !MODE_FORCE)
            return false;

        Wrapper<Boolean> acceptable = new Wrapper<>(true);
        if (biDir) {
            for (ItemStack ore : ores) {
                boolean noneMatch = true;
                for (ILabel label : labels) {
                    if (checkMatch(label, ore)) {
                        noneMatch = false;
                        break;
                    }
                }
                if (noneMatch) {
                    acceptable.value = false;
                    break;
                }
            }
        }
        for (ILabel label : labels) {
            boolean noneMatch = true;
            for (ItemStack ore : ores) {
                if (checkMatch(label, ore)) {
                    noneMatch = false;
                    break;
                }
            }
            if (noneMatch) {
                acceptable.value = false;
                break;
            }
        }
        return acceptable.value;
    }

    private static boolean checkMatch(ILabel l, ItemStack o) {
        return l instanceof LItemStack && OreDictionary.itemMatches(o, ((LItemStack) l).getRep(), false);
    }

    @Override
    @Nonnull
    public ItemStack getRepresentation() {
        NonNullList<ItemStack> list = NonNullList.create();
        for (ItemStack ore : OreDictionary.getOres(name)) {
            if (ore.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                Item item = ore.getItem();
                item.getSubItems(item, CreativeTabs.tabAllSearch, list);
            } else {
                list.add(ore);
            }
        }
        if (list.isEmpty())
            return ItemStackHelper.EMPTY_ITEM_STACK;
        long index = System.currentTimeMillis() / 1500;
        return list.get((int) (index % list.size()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return Utilities.I18n.get("label.ore_dict.name", name);
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean matches(Object l) {
        return l instanceof LOreDict && name.equals(((LOreDict) l).name) && super.matches(l);
    }

    @Override
    public LOreDict copy() {
        return new LOreDict(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecaGui gui) {
        gui.drawItemStack(0, 0, getRepresentation(), false);
        // TODO some item won't render correctly if has this overlay
        // gui.drawResource(Resource.LBL_FRAME, 0, 0);
    }

    @Override
    public NBTTagCompound toNbt() {
        NBTTagCompound ret = super.toNbt();
        ret.setString(KEY_NAME, name);
        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + JustEnoughCalculation.Reference.MODNAME);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ super.hashCode();
    }

    public String getName() {
        return name;
    }
}
