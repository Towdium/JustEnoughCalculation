package me.towdium.jecalculation.gui;

import me.towdium.jecalculation.JECConfig;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

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
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new GuiConfig(parentScreen,
                Collections.singletonList(new ConfigElement(JECConfig.config.getCategory(JECConfig.EnumCategory.General.toString()))),
                JustEnoughCalculation.Reference.MODID, false, false, GuiConfig.getAbridgedConfigPath(JECConfig.config.toString()));
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
