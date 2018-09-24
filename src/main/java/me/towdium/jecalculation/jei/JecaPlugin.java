package me.towdium.jecalculation.jei;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiRecipe;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
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
public class JecaPlugin implements IModPlugin {
    public static IJeiRuntime runtime;

    public static ILabel getLabelUnderMouse() {
        Object o = runtime.getIngredientListOverlay().getIngredientUnderMouse();
        return ILabel.Converter.from(o);
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
            return JecaGui.ContainerTransfer.class;
        }

        @Nullable
        @Override
        public IRecipeTransferError transferRecipe(
                Container container, IRecipeLayout recipeLayout, EntityPlayer player,
                boolean maxTransfer, boolean doTransfer) {
            if (doTransfer && container instanceof JecaGui.JecContainer) {
                JecaGui gui = ((JecaGui.JecContainer) container).getGui();
                if (gui.root instanceof GuiRecipe) {
                    ((GuiRecipe) gui.root).transfer(recipeLayout);
                } else {
                    GuiRecipe guiRecipe = new GuiRecipe();
                    JecaGui.displayGui(true, true, guiRecipe);
                    guiRecipe.transfer(recipeLayout);
                }
                return null;
            } else return null;
        }
    }
}
