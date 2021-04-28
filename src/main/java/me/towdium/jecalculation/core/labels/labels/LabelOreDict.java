package me.towdium.jecalculation.core.labels.labels;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.drawables.DContainer;
import me.towdium.jecalculation.client.gui.drawables.DText;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.polyfill.mc.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-10.
 */
public class LabelOreDict extends LabelSimpleAmount {
    public static final String IDENTIFIER = "oreDict";
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
        return "Ore: " + name; // localization
    }

    public static RegistryEditor.IEditor getEditor() {
        return new Editor();
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
                is.getItem().getSubItems(is.getItem(), CreativeTabs.tabAllSearch, list);
            } else list.add(is);
        });
        long index = System.currentTimeMillis() / 1500;

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

    public static class Editor extends DContainer implements RegistryEditor.IEditor {
        Consumer<ILabel> label;

        public Editor() {
            add(new DText(5, 5, JecGui.Font.DEFAULT_NO_SHADOW, "hello"));
        }

        @Override
        public void setCallback(Consumer<ILabel> callback) {
            label = callback;
        }
    }
}
