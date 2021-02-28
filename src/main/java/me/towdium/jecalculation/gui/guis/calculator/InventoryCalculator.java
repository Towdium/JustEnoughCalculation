package me.towdium.jecalculation.gui.guis.calculator;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

/**
 * @author Towdium
 */
public class InventoryCalculator extends InventoryBasic {

    public InventoryCalculator(ItemStack itemStack) {
        super("Calculator", false, 37);
    }
}
