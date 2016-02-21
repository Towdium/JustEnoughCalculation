package pers.towdium.tudicraft.gui.calculator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

/**
 * @author Towdium
 */
public class InventoryCalculator extends InventoryBasic{

    public InventoryCalculator(ItemStack itemStack) {
        super("Calculator", false, 37);
    }
}
