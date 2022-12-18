package me.towdium.jecalculation.nei;

import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import cpw.mods.fml.common.Loader;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Version;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class NEIPlugin {

    private static boolean catalystEnabled = false;

    private static final Version CATALYST_NEI_VERSION = new Version("2.1.0-GTNH");

    public static void init() {
        GuiContainerManager.addTooltipHandler(new JecaTooltipHandler());
        // nei version check
        String neiVersion =
                Loader.instance().getIndexedModList().get("NotEnoughItems").getVersion();
        JustEnoughCalculation.logger.info("NEI version: " + neiVersion);
        Version version = new Version(neiVersion);
        if (version.isSuccess() && version.compareTo(CATALYST_NEI_VERSION) >= 0) {
            NEIPlugin.catalystEnabled = true;
            JustEnoughCalculation.logger.info("catalyst enabled");
        } else {
            JustEnoughCalculation.logger.info("catalyst disabled");
        }
    }

    private static ItemStack currentItemStack;

    public static boolean isCatalystEnabled() {
        return catalystEnabled;
    }

    public static ILabel getLabelUnderMouse() {
        if (NEIPlugin.currentItemStack == null) return ILabel.EMPTY;
        Object stack = Adapter.convertFluid(NEIPlugin.currentItemStack);
        return ILabel.Converter.from(stack);
    }

    public static void setLabelUnderMouse(ItemStack itemStack) {
        NEIPlugin.currentItemStack = itemStack;
    }

    public static boolean openRecipeGui(Object rep, boolean usage) {
        if ((rep instanceof ItemStack || rep instanceof FluidStack)) {
            String id = rep instanceof ItemStack ? "item" : "liquid";
            if (!usage) {
                return GuiCraftingRecipe.openRecipeGui(id, rep);
            } else {
                return GuiUsageRecipe.openRecipeGui(id, rep);
            }
        } else if (rep != null) {
            JustEnoughCalculation.logger.warn("unknown label representation " + rep);
        }
        return false;
    }
}
