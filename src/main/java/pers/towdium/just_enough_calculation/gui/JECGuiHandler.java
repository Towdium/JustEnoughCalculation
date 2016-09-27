package pers.towdium.just_enough_calculation.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import pers.towdium.just_enough_calculation.gui.guis.GuiCalculator;
import pers.towdium.just_enough_calculation.gui.guis.GuiMathCalculator;

/**
 * Author: Towdium
 * Date:   2016/8/13.
 */

public class JECGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GuiId.CRAFTING_CALCULATOR:
                return new GuiCalculator(null);
            case GuiId.MATH_CALCULATOR:
                return new GuiMathCalculator(null);
        }
        return null;
    }

    public static final class GuiId {
        public static final int CRAFTING_CALCULATOR = 0;
        public static final int MATH_CALCULATOR = 1;
    }
}
