package pers.towdium.justEnoughCalculation.plugin;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.DefaultOverlayHandler;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.gui.guis.recipeEditor.GuiRecipeEditor;


public class NEICalculatorConfig implements IConfigureNEI {
    @Override
    public void loadConfig() {
        LOOP:
        for(String s : JustEnoughCalculation.JECConfig.EnumItems.ListRecipeCategory.getProperty().getStringList()){
            for(String b : JustEnoughCalculation.JECConfig.EnumItems.ListRecipeBlackList.getProperty().getStringList()){
                if(s.equals(b)){
                    continue LOOP;
                }
            }
            API.registerGuiOverlay(GuiRecipeEditor.class, s);
            API.registerGuiOverlayHandler(GuiRecipeEditor.class, new MyOverlayHandler(), s);
        }
        //API.registerGuiOverlay(GuiRecipeEditor.class, "crafting");
        //API.registerGuiOverlayHandler(GuiRecipeEditor.class, new MyOverlayHandler(), "crafting");
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
