package pers.towdium.justEnoughCalculation.core;

import net.minecraft.item.ItemStack;

/**
 * Author:  Towdium
 * Created: 2016/6/14.
 */
public class ItemStackHelper {
    public static boolean isTypeEqual(ItemStack one, ItemStack two){
        return one != null && two != null && one.getItem() == two.getItem() && one.getMetadata() == two.getMetadata();
    }
}
