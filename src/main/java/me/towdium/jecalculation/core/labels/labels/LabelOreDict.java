package me.towdium.jecalculation.core.labels.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.DText;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-10.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LabelOreDict extends LabelSimpleAmount {
    public static final String KEY_NAME = "name";
    public static final String KEY_AMOUNT = "amount";

    protected String name;

    public LabelOreDict(String name) {
        this(name, 1);
    }

    public LabelOreDict(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public LabelOreDict(LabelOreDict eod) {
        this.name = eod.name;
        this.amount = eod.amount;
    }

    public LabelOreDict(NBTTagCompound nbt) {
        name = nbt.getString(KEY_NAME);
        amount = nbt.getInteger(KEY_AMOUNT);
    }

    @Override
    public String getDisplayName() {
        return Utilities.L18n.format("label.ore_dict.name", name);
    }

    public static List<ILabel> guess(List<ItemStack> iss) {
        HashSet<Integer> ids = new HashSet<>();
        for (int i : OreDictionary.getOreIDs(iss.get(0))) ids.add(i);
        iss.forEach(is -> {
            for (int i : OreDictionary.getOreIDs(is))
                if (!ids.contains(i)) ids.remove(i);
        });
        return ids.stream().map(i -> new LabelOreDict(OreDictionary.getOreName(i))).collect(Collectors.toList());
    }

    @Override
    public LabelOreDict copy() {
        return new LabelOreDict(this);
    }

    @Override
    public void drawEntry(JecGui gui) {
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

    public static RegistryEditor.IEditor getEditor() {
        return new Editor();
    }

    @Override
    public List<String> getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + JustEnoughCalculation.Reference.MODNAME);
        return existing;
    }

    public static class Editor extends RegistryEditor.Editor {
        public Editor() {
            add(new DText(5, 5, JecGui.Font.DEFAULT_NO_SHADOW, "hello"));
        }

        @Override
        public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
            callback.value.accept(new LabelOreDict("plankWood"));
            return super.onClicked(gui, xMouse, yMouse, button);
        }
    }
}
