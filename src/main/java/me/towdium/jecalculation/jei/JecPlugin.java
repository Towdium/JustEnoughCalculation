package me.towdium.jecalculation.jei;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.core.entry.Entry;
import me.towdium.jecalculation.core.entry.entries.EntryItemStack;
import me.towdium.jecalculation.utils.Utilities.ReversedIterator;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-8-23.
 */
@mezz.jei.api.JEIPlugin
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JecPlugin implements IModPlugin {
    public static final TransferRegistryItem registryItem = TransferRegistryItem.INSTANCE;
    public static final TransferRegistryFluid registryFluid = TransferRegistryFluid.INSTANCE;
    public static IJeiRuntime runtime;

    public static Entry getEntryUnderMouse() {
        Object o = runtime.getIngredientListOverlay().getIngredientUnderMouse();
        if (o == null) return Entry.EMPTY;
        else if (o instanceof ItemStack) return registryItem.toEntry(((ItemStack) o));
        else if (o instanceof FluidStack) return registryFluid.toEntry(((FluidStack) o));
        else {
            JustEnoughCalculation.logger.warn("Unsupported ingredient type detected: " + o.getClass());
            return Entry.EMPTY;
        }
    }

    public static List<Entry> transferRawItemStack(List<ItemStack> iss) {
        return iss.stream().map(EntryItemStack::new).collect(Collectors.toList());
    }

    @Override
    public void register(IModRegistry registry) {
        registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(new TransferHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    public static class TransferHandler implements IRecipeTransferHandler {
        @Override
        public Class getContainerClass() {
            return JecGui.ContainerTransfer.class;
        }

        @Nullable
        @Override
        public IRecipeTransferError transferRecipe(
                Container container, IRecipeLayout recipeLayout, EntityPlayer player,
                boolean maxTransfer, boolean doTransfer) {
            return null;  // TODO
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
            return new ReversedIterator<>(handlers).stream().flatMap(h -> h.apply(ingredients).stream())
                    .collect(Collectors.toList());
        }

        void register(Function<List<T>, List<Entry>> handler) {
            handlers.add(handler);
        }
    }

    public static class TransferRegistryItem extends TransferHandlerRegistry<ItemStack> {
        public static final TransferRegistryItem INSTANCE;

        static {
            INSTANCE = new TransferRegistryItem();

            INSTANCE.register(JecPlugin::transferRawItemStack);
        }

        private TransferRegistryItem() {
        }
    }

    public static class TransferRegistryFluid extends TransferHandlerRegistry<FluidStack> {
        public static final TransferRegistryFluid INSTANCE;

        static {
            INSTANCE = new TransferRegistryFluid();
        }

        private TransferRegistryFluid() {
        }
    }

}
