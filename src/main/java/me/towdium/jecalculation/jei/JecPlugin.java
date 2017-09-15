package me.towdium.jecalculation.jei;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.core.labels.ILabel;
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

/**
 * Author: towdium
 * Date:   17-8-23.
 */
@mezz.jei.api.JEIPlugin
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JecPlugin implements IModPlugin {
    public static final ILabel.RegistryConverterItem registryItem = ILabel.RegistryConverterItem.INSTANCE;
    public static final ILabel.RegistryConverterFluid registryFluid = ILabel.RegistryConverterFluid.INSTANCE;
    public static IJeiRuntime runtime;

    public static ILabel getEntryUnderMouse() {
        Object o = runtime.getIngredientListOverlay().getIngredientUnderMouse();
        if (o == null) return ILabel.EMPTY;
        else if (o instanceof ItemStack) return registryItem.toEntry(((ItemStack) o));
        else if (o instanceof FluidStack) return registryFluid.toEntry(((FluidStack) o));
        else {
            JustEnoughCalculation.logger.warn("Unsupported ingredient type detected: " + o.getClass());
            return ILabel.EMPTY;
        }
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



}
