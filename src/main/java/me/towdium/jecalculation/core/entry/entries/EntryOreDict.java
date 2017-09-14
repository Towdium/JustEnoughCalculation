package me.towdium.jecalculation.core.entry.entries;

import me.towdium.jecalculation.client.gui.JecGui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Author: towdium
 * Date:   17-9-10.
 */
public class EntryOreDict extends EntrySimpleAmount {
    public static final String IDENTIFIER = "ore";

    protected String name;

    public EntryOreDict(String name) {
        this(name, 1);
    }

    public EntryOreDict(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public EntryOreDict(EntryOreDict eod) {
        this.name = eod.name;
        this.amount = eod.amount;
    }

    @Override
    public String getDisplayName() {
        return "Ore: " + name; // localization
    }

    @Override
    public EntryOreDict copy() {
        return new EntryOreDict(this);
    }

    public String getName() {
        return name;
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

        GlStateManager.pushMatrix();
        GlStateManager.translate(1, 1, 0);
        GlStateManager.scale(14f / 16, 14f / 16, 1);
        gui.drawItemStack(0, 0, list.get((int) (index % list.size())), false);
        GlStateManager.popMatrix();
    }
}
