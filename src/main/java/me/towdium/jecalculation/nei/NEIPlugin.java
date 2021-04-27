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
    public static final TransferRegistryItem registryItem = TransferRegistryItem.INSTANCE;

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

    public static List<Entry> transferRawItemStack(List<ItemStack> iss) {
        return iss.stream().map(EntryItemStack::new).collect(Collectors.toList());
    }

    public static class TransferRegistryItem extends TransferHandlerRegistry<ItemStack> {
        public static final TransferRegistryItem INSTANCE;

        static {
            INSTANCE = new TransferRegistryItem();

            INSTANCE.register(NEIPlugin::transferRawItemStack);
        }

        private TransferRegistryItem() {
        }
    }

    public static class TransferHandlerRegistry<T> {
        List<Function<List<T>, List<Entry>>> handlers = new ArrayList<>();

        private TransferHandlerRegistry() {
        }

        Entry toEntry(T ingredient) {
            return handlers.isEmpty() ? Entry.EMPTY :
                   handlers.get(0).apply(Collections.singletonList(ingredient)).get(0);
        }

        List<Entry> toEntry(List<T> ingredients) {
            return new Utilities.ReversedIterator<>(handlers).stream().flatMap(h -> h.apply(ingredients).stream())
                                                             .collect(Collectors.toList());
        }

        void register(Function<List<T>, List<Entry>> handler) {
            handlers.add(handler);
        }
    }
}
