package me.towdium.jecalculation.nei;

import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.*;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import scala.tools.cmd.Opt;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NEIPlugin {

    public static void init() {
        GuiContainerManager.addTooltipHandler(new JecaTooltipHandler());
    }

    private static ItemStack currentItemStack;

    public static ILabel getLabelUnderMouse() {
        if (NEIPlugin.currentItemStack == null) return ILabel.EMPTY;
        return ILabel.Converter.from(NEIPlugin.currentItemStack);
    }

    public static void setLabelUnderMouse(ItemStack itemStack) {
        NEIPlugin.currentItemStack = itemStack;
    }

    public static Optional<ItemStack> getCatalyst(@Nonnull IRecipeHandler handler) {
        final String handlerName = handler.toString().split("@")[0];
        final String handlerID;
        if(handler instanceof TemplateRecipeHandler) {
            handlerID = (((TemplateRecipeHandler)handler).getOverlayIdentifier());
        } else {
            handlerID = null;
        }
        HandlerInfo info = GuiRecipeTab.getHandlerInfo(handlerName, handlerID);

        if(info == null) {
            return Optional.empty();
        }
        ItemStack itemStack = info.getItemStack();
        return Optional.ofNullable(itemStack);
    }

    public static void openRecipeGui(Object rep, int button) {
        if ((rep instanceof ItemStack || rep instanceof FluidStack)) {
            String id = rep instanceof ItemStack ? "item" : "liquid";
            if (button == 0) {
                GuiCraftingRecipe.openRecipeGui(id, rep);
            } else if (button == 1) {
                GuiUsageRecipe.openRecipeGui(id, rep);
            } else {
                // TODO check
                JustEnoughCalculation.logger.warn("unknown button " + button);
            }
        } else if (rep != null) {
            JustEnoughCalculation.logger.warn("unknown label representation " + rep);
        }
    }




}
