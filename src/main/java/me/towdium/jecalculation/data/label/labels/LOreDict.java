package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.polyfill.mc.util.NonNullList;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
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
    public static final String KEY_AMOUNT = "amount";
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

    public boolean isEmpty() {
        return OreDictionary.getOres(name).isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack getRepresentation() {
        NonNullList<ItemStack> list = NonNullList.create();
        OreDictionary.getOres(name).forEach(is -> {
            if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                is.getItem().getSubItems(is.getItem(), CreativeTabs.tabAllSearch, list);
            } else
                list.add(is);
        });
        if (list.isEmpty()) return ItemStackHelper.EMPTY_ITEM_STACK;
        long index = System.currentTimeMillis() / 1500;
        return list.get((int) (index % list.size()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return Utilities.I18n.format("label.ore_dict.name", name);
    }

    public static boolean mergeSame(ILabel a, ILabel b) {
        if (a instanceof LOreDict && b instanceof LOreDict) {
            LOreDict lodA = (LOreDict) a;
            LOreDict lodB = (LOreDict) b;
            return lodA.getName().equals(lodB.getName());
        } else return false;
    }

    public static boolean mergeFuzzy(ILabel a, ILabel b) {
        if (a instanceof LOreDict && b instanceof LItemStack) {
            LOreDict lod = (LOreDict) a;
            LItemStack lis = (LItemStack) b;
            return OreDictionary.getOres(lod.name).stream()
                                .map(Converter::from)
                                .anyMatch(i -> LItemStack.merge(i, lis));
        }
        return false;
    }

    public static List<ILabel> guess(List<ILabel> iss) {
        ILabel l = iss.get(0);
        HashSet<Integer> ids = new HashSet<>();
        long amount = l.getAmount();
        for (int i : OreDictionary.getOreIDs(((LItemStack) l).getRep()))
            if (check(i, iss))
                ids.add(i);
        return ids.stream().map(i -> new LOreDict(OreDictionary.getOreName(i), amount)).collect(Collectors.toList());
    }

    // check labels in the list suitable for the ore id
    private static boolean check(int id, List<ILabel> labels) {
        ArrayList<ItemStack> ores = OreDictionary.getOres(OreDictionary.getOreName(id));
        if (labels.size() == 1 && ores.size() == 1 &&
            (ores.get(0).getItemDamage() == OreDictionary.WILDCARD_VALUE || !MODE_FORCE))
            return false;

        boolean acceptable = true;
        for (ItemStack ore : ores) {
            boolean found = false;
            for (ILabel label : labels) {
                if (label instanceof LItemStack &&
                    OreDictionary.itemMatches(ore, ((LItemStack) label).getRep(), false)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                acceptable = false;
                break;
            }
        }
        for (ILabel label : labels) {
            boolean found = false;
            for (ItemStack ore : ores) {
                if (label instanceof LItemStack &&
                    OreDictionary.itemMatches(ore, ((LItemStack) label).getRep(), false)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                acceptable = false;
                break;
            }
        }
        return acceptable;
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
        NonNullList<ItemStack> list = NonNullList.create();
        OreDictionary.getOres(name).forEach(is -> {
            if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                is.getItem().getSubItems(is.getItem(), CreativeTabs.tabAllSearch, list);
            } else
                list.add(is);
        });
        if (list.isEmpty())
            return;
        long index = System.currentTimeMillis() / 1500;
        //GlStateManager.pushMatrix();
        //GlStateManager.translate(1, 1, 0);
        //GlStateManager.scale(14f / 16, 14f / 16, 1);
        gui.drawItemStack(0, 0, list.get((int) (index % list.size())), false);
        //GlStateManager.popMatrix();
        gui.drawResource(Resource.LBL_FRAME, 0, 0);
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
