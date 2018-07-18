package me.towdium.jecalculation.data.label.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-10.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LOreDict extends LabelSimpleAmount {
    public static final String IDENTIFIER = "oreDict";
    public static final String KEY_NAME = "name";
    public static final String KEY_AMOUNT = "amount";
    public static final boolean MODE_FORCE = false;

    static {

    }

    protected String name;

    public LOreDict(String name) {
        this(name, 1);
    }

    public LOreDict(String name, int amount) {
        super(amount);
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

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return Utilities.I18n.format("label.ore_dict.name", name);
    }

    public static List<ILabel> guess(List<ILabel> iss) {
        ILabel l = iss.get(0);
        if ((MODE_FORCE || iss.size() > 1) && l instanceof LItemStack) {
            HashSet<Integer> ids = new HashSet<>();
            int amount = ((LItemStack) l).getAmount();
            for (int i : OreDictionary.getOreIDs(((LItemStack) l).itemStack))
                if (check(i, iss)) ids.add(i);
            return ids.stream().map(i -> new LOreDict(OreDictionary.getOreName(i), amount))
                    .collect(Collectors.toList());
        } else return new ArrayList<>();
    }

    // check labels in the list suitable for the ore id
    private static boolean check(int id, List<ILabel> labels) {
        NonNullList<ItemStack> ores = OreDictionary.getOres(OreDictionary.getOreName(id));
        for (ItemStack ore : ores) if (!check(ore, labels)) return false;
        return true;
    }

    // check the recorded ore item is in the list
    private static boolean check(ItemStack ore, List<ILabel> labels) {
        for (ILabel label : labels)
            if (label instanceof LItemStack && OreDictionary.itemMatches(ore, ((LItemStack) label).itemStack, false))
                return true;
        return false;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public LOreDict copy() {
        return new LOreDict(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecGui gui) {
        NonNullList<ItemStack> list = NonNullList.create();
        OreDictionary.getOres(name).forEach(is -> {
            if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                is.getItem().getSubItems(CreativeTabs.SEARCH, list);
            } else list.add(is);
        });
        long index = System.currentTimeMillis() / 1500;
        gui.drawResource(Resource.LBL_ORE_DICT, 0, 0);
        GlStateManager.pushMatrix();
        GlStateManager.translate(1, 1, 0);
        GlStateManager.scale(14f / 16, 14f / 16, 1);
        gui.drawItemStack(0, 0, list.get((int) (index % list.size())), false);
        GlStateManager.popMatrix();
    }

    @Override
    public NBTTagCompound toNBTTagCompound() {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setString(KEY_NAME, name);
        ret.setInteger(KEY_AMOUNT, amount);
        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + JustEnoughCalculation.Reference.MODNAME);
        return existing;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ amount;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LOreDict
                && amount == ((LOreDict) obj).amount && name.equals(((LOreDict) obj).name);
    }

    public String getName() {
        return name;
    }
}
