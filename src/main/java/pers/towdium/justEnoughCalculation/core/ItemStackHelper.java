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

    public static class Click {
        public static boolean leftClick(ItemStack itemStack, boolean doChange) {
            return true;
        }

        public static boolean leftShift(ItemStack itemStack, boolean doChange) {
            return true;
        }

        public static boolean rightClick(ItemStack itemStack, boolean doChange) {
            return true;
        }

        public static boolean rightShift(ItemStack itemStack, boolean doChange) {
            return true;
        }
    }
}
