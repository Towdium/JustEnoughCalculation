package pers.towdium.just_enough_calculation.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import pers.towdium.just_enough_calculation.JECConfig;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;

import java.util.Collections;
import java.util.Set;

/**
 * Author: Towdium
 * Date:   2016/8/14.
 */
public class JECGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ConfigGUI.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    public static class ConfigGUI extends GuiConfig {
        public ConfigGUI(GuiScreen parent) {
            super(parent,
                    Collections.singletonList(new ConfigElement(JECConfig.config.getCategory(JECConfig.EnumCategory.General.toString()))),
                    JustEnoughCalculation.Reference.MODID, false, false, GuiConfig.getAbridgedConfigPath(JECConfig.config.toString()));
        }
    }
}
