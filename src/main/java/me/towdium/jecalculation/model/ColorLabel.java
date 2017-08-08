package me.towdium.jecalculation.model;

import me.towdium.jecalculation.item.ItemLabel;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Author: towdium
 * Date:   08/03/17.
 */
public class ColorLabel implements IItemColor {
    @Override
    public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            return Color.WHITE.getRGB();
        } else {
            String name = ItemLabel.getName(stack);
            return name == null ? Color.WHITE.getRGB() : name.hashCode() * 0x12345678;
        }
    }
}
