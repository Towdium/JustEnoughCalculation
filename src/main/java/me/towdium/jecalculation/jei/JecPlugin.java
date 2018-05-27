package me.towdium.jecalculation.jei;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.guis.GuiRecipe;
import me.towdium.jecalculation.data.label.ILabel;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public static IJeiRuntime runtime;

    public static ILabel getLabelUnderMouse() {
        Object o = runtime.getIngredientListOverlay().getIngredientUnderMouse();
        if (o == null) return ILabel.EMPTY;
        else if (o instanceof ItemStack) return ILabel.CONVERTER_ITEM.toLabel(((ItemStack) o));
        else if (o instanceof FluidStack) return ILabel.CONVERTER_FLUID.toLabel(((FluidStack) o));
        else {
            JustEnoughCalculation.logger.warn("Unsupported ingredient type detected: " + o.getClass());
            return ILabel.EMPTY;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void register(IModRegistry registry) {
        registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(new TransferHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @SideOnly(Side.CLIENT)
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
            if (doTransfer && container instanceof JecGui.JecContainer) {
                JecGui gui = ((JecGui.JecContainer) container).getGui();
                if (gui.root instanceof GuiRecipe) {
                    ((GuiRecipe) gui.root).transfer(recipeLayout);
                } else {
                    GuiRecipe guiRecipe = new GuiRecipe();
                    JecGui.displayGui(true, true, guiRecipe);
                    guiRecipe.transfer(recipeLayout);
                }
                return null;
            } else return null;
        }
    }
}
