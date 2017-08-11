package me.towdium.jecalculation.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Author: Towdium
 * Date:   2016/6/25.
 */
@SuppressWarnings("unused")
public class Utilities {
    final static int[] scaleTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
    static Map<String, String> dictionary = new HashMap<>();

    static {
        Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
        for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
            String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
            String modName = modEntry.getValue().getName();
            dictionary.put(lowercaseId, modName);
        }
    }

    // FLOAT FORMATTING
    public static String cutFloat(float f, int size) {
        BiFunction<Float, Integer, String> form = (fl, len) -> {
            // here 2 means floating point and unit character
            // represents for maximum acceptable decimal digits
            int scale = len - 2 - String.valueOf(fl.intValue()).length();
            // maximum decimal limits to 2
            int cut = scale > 2 ? 2 : scale;
            return String.format("%." + (cut < 0 ? 0 : cut) + 'f', fl);
        };
        int scale = (int) Math.log10(f) / 3;
        switch (scale) {
            case 0:
                return form.apply(f, size);
            case 1:
                return form.apply(f / 1000.0f, size) + 'K';
            case 2:
                return form.apply(f / 1000000.0f, size) + 'M';
            case 3:
                return form.apply(f / 1000000000.0f, size) + 'B';
            case 4:
                return form.apply(f / 1000000000000.0f, size) + 'G';
            default:
                return form.apply(f / 1000000000000000.0f, size) + 'T';
        }
    }

    public static String cutLong(long i, int size) {
        if (i < 1000) {
            return String.valueOf(i);
        } else {
            return cutFloat(i, size);
        }
    }

    // CIRCULATE STRUCTURE
    public static int circulate(int current, int total, boolean forward) {
        if (forward) {
            if (current == total - 1) return 0;
            else return current + 1;
        } else {
            if (current == 0) return total - 1;
            else return current - 1;
        }
    }

    // MOD NAME
    public static String getModName(ItemStack stack) {
        ResourceLocation tmp = stack.getItem().getRegistryName();
        if (tmp == null) return null;

        String name = tmp.toString();
        name = name.substring(0, name.indexOf(":"));
        if (name.equals("minecraft")) return "Minecraft";
        else return dictionary.get(name);
    }

    public static String getModName(FluidStack stack) {
        String name = stack.getFluid().getName();
        if (name.equals("lava") || name.equals("water")) return "Minecraft";
        else return dictionary.get(stack.getFluid().getStill().toString().split(":")[0]);
    }
}
