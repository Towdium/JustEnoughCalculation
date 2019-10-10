package me.towdium.jecalculation.jei;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiRecipe;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-23.
 */
@JeiPlugin
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JecaPlugin implements IModPlugin {
    public static IJeiRuntime runtime;

    public static ILabel getLabelUnderMouse() {
        Wrapper<Object> o = new Wrapper<>(null);
        o.push(runtime.getIngredientListOverlay().getIngredientUnderMouse());
        o.push(runtime.getBookmarkOverlay().getIngredientUnderMouse());
        return ILabel.Converter.from(o.value);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(JustEnoughCalculation.MODID, "general");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addUniversalRecipeTransferHandler(new TransferHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    public static class TransferHandler implements IRecipeTransferHandler {
        @Override
        public Class getContainerClass() {
            return JecaGui.ContainerTransfer.class;
        }

        @Nullable
        @Override
        public IRecipeTransferError transferRecipe(
                Container container, IRecipeLayout recipeLayout, PlayerEntity player,
                boolean maxTransfer, boolean doTransfer) {
            if (doTransfer && container instanceof JecaGui.JecaContainer) {
                JecaGui gui = ((JecaGui.JecaContainer) container).getGui();
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
