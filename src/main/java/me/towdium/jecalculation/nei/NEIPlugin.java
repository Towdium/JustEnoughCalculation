package me.towdium.jecalculation.nei;

import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipeTab;
import codechicken.nei.recipe.HandlerInfo;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import me.towdium.jecalculation.data.label.ILabel;
import net.minecraft.item.ItemStack;
import scala.tools.cmd.Opt;

import javax.annotation.Nonnull;
import java.util.Optional;

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
}
