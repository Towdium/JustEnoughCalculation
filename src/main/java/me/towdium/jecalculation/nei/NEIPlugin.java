package me.towdium.jecalculation.nei;

import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import me.towdium.jecalculation.core.entry.Entry;
import me.towdium.jecalculation.core.entry.entries.EntryItemStack;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NEIPlugin {
    public static final Entry.ConverterRegistryItem registryItem = Entry.ConverterRegistryItem.INSTANCE;
    public static final Entry.ConverterRegistryFluid registryFluid = Entry.ConverterRegistryFluid.INSTANCE;

    public static void init() {
        GuiContainerManager.addTooltipHandler(new JecaTooltipHandler());
    }

    private static ItemStack currentItemStack;

    public static Entry getEntryUnderMouse() {
        if (NEIPlugin.currentItemStack == null) return Entry.EMPTY;
        else return registryItem.toEntry(NEIPlugin.currentItemStack);
    }

    public static void setEntryUnderMouse(ItemStack itemStack) {
        NEIPlugin.currentItemStack = itemStack;
    }
}
