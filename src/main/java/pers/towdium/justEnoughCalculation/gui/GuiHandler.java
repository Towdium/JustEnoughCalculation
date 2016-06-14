package pers.towdium.justEnoughCalculation.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import pers.towdium.justEnoughCalculation.gui.guis.GuiCalculator;

/**
 * Author:  Towdium
 * Created: 2016/6/13.
 */
public class GuiHandler implements IGuiHandler {
    public static final class GuiId{
        public static final int CALCULATOR = 0;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GuiId.CALCULATOR: return new GuiCalculator(null);
        }
        return null;
    }
}
