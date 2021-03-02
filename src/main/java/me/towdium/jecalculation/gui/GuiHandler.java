package me.towdium.jecalculation.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import me.towdium.jecalculation.gui.guis.GuiCalculator;
import me.towdium.jecalculation.gui.guis.GuiEditor;
import me.towdium.jecalculation.gui.guis.GuiRecipeSearch;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    public static final class GuiId {
        public static final int CALCULATOR = 0;
        public static final int RECIPE_SEARCH = 1;
        public static final int EDITOR = 2;

    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case GuiId.CALCULATOR:
                return new GuiCalculator(null);
            case GuiId.RECIPE_SEARCH:
                return new GuiRecipeSearch(Minecraft.getMinecraft().currentScreen);
            case GuiId.EDITOR:
                return new GuiEditor(null);
        }
        return null;
    }
}
