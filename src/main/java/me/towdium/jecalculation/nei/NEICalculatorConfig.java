package me.towdium.jecalculation.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.JecaGui;

import java.util.stream.Stream;

public class NEICalculatorConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        String[] baseOverlayIdentifiers = new String[]{"crafting", "crafting2x2", "smelting", "fuel", "brewing"};
        String[] ae2OverlayIdentifiers = new String[]{"inscriber", "grindstone"};
        /* For the recipeHandlers which extend `TemplateRecipeHandler` without override the `getOverlayIdentifier` function */
        String[] nullOverlayIdentifiers = new String[]{null};
        Stream.of(baseOverlayIdentifiers, ae2OverlayIdentifiers, nullOverlayIdentifiers)
              .flatMap(Stream::of)
              .forEach(ident -> {
                  API.registerGuiOverlay(JecaGui.class, ident);
                  API.registerGuiOverlayHandler(JecaGui.class, new JecaOverlayHandler(), ident);
              });
    }

    @Override
    public String getName() {
        return JustEnoughCalculation.Reference.MODNAME;
    }

    @Override
    public String getVersion() {
        return JustEnoughCalculation.Reference.VERSION;
    }
}
