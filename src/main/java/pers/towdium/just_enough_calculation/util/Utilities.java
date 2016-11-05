package pers.towdium.just_enough_calculation.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import pers.towdium.just_enough_calculation.item.ItemFluidContainer;
import pers.towdium.just_enough_calculation.util.function.TriFunction;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.wrappers.Singleton;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Author: Towdium
 * Date:   2016/6/25.
 */
public class Utilities {
    final static int[] scaleTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
    static Map<String, String> dictionary = new HashMap<>();

    // FOR STRING FORMATTING

    static {
        Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
        for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
            String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
            String modName = modEntry.getValue().getName();
            dictionary.put(lowercaseId, modName);
        }
    }

    public static String cutFloat(float f, int size) {
        TriFunction<Float, Integer, Integer, String> form = (fl, len, max) -> {
            int scale = len - 2 - String.valueOf(fl.intValue()).length();
            int cut = scale > max ? max : scale;
            return String.format("%." + (cut < 0 ? 0 : cut) + 'f', fl);
        };
        int scale = (int) Math.log10(f) / 3;
        switch (scale) {
            case 0:
                return form.apply(f, size, 2);
            case 1:
                return form.apply(f / 1000.0f, size, 2) + 'K';
            case 2:
                return form.apply(f / 1000000.0f, size, 2) + 'M';
            case 3:
                return form.apply(f / 1000000000.0f, size, 2) + 'B';
            case 4:
                return form.apply(f / 1000000000000.0f, size, 2) + 'G';
            default:
                return form.apply(f / 1000000000000000.0f, size, 2) + 'T';
        }
    }

    public static String cutLong(long i, int size) {
        if (i < 1000) {
            return String.valueOf(i);
        } else {
            return cutFloat(i, size);
        }
    }

    // REFLECTION

    public static String cutString(String s, int length, FontRenderer fontRenderer) {
        return fontRenderer.getStringWidth(s) <= length ? s : fontRenderer.trimStringToWidth(s, length - 6) + "...";
    }

    @SuppressWarnings("unchecked")
    public static <T, C> T getField(C o, String... names) {
        Field field = null;
        boolean flag = false;
        for (String name : names) {
            try {
                field = o.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                continue;
            }
            flag = true;
            break;
        }
        if (!flag) {
            String buffer = "Field not found in class " + o.getClass().getCanonicalName() + ":";
            for (String s : names) {
                buffer += " ";
                buffer += s;
            }
            throw new NoSuchFieldError(buffer);
        } else {
            field.setAccessible(true);
            try {
                Object temp = field.get(o);
                return (T) temp;
            } catch (IllegalAccessException e) {
                return null;
            }
        }
    }

    public static Object getField(Class c, Object o, String... names) {
        Field f = null;
        for (String name : names) {
            try {
                f = c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (f == null) {
            String s = "Field ";
            for (String name : names) {
                s += "\"";
                s += name;
                s += "\",";
            }
            throw new NoSuchFieldError(s.substring(0, s.length() - 1) + " not found in class " + c.getName());
        }
        f.setAccessible(true);
        try {
            o = f.get(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static <D, S> void setField(D dest, S source, String... name) {
        setField(dest.getClass(), dest, source, name);
    }
    // CIRCULATE STRUCTURE

    public static void setField(Class c, Object dest, Object source, String... name) {
        Singleton<Field> f = new Singleton<>(null);
        for(String s : name) {
            try {
                f.push(c.getDeclaredField(s));
            } catch (NoSuchFieldException ignored) {}
        }
        Field field = f.value;
        if (field == null) {
            String buffer = "Field not found in class " + dest.getClass().getCanonicalName() + ":";
            for (String s : name) {
                buffer += " ";
                buffer += s;
            }
            throw new NoSuchFieldError(buffer);
        }
        field.setAccessible(true);
        Field modifiersField;
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(dest, source);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // GUI

    public static int circulate(int current, int total, boolean forward) {
        if (forward) {
            if (current == total - 1)
                return 0;
            else
                return current + 1;
        } else {
            if (current == 0)
                return total - 1;
            else
                return current - 1;
        }
    }

    // MOD NAME

    public static void openGui(GuiScreen gui) {
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    // MATH

    public static String getModName(ItemStack stack) {
        String ret;
        if (stack.getItem() instanceof ItemFluidContainer) {
            Fluid fluid = ItemStackHelper.NBT.getFluid(stack);
            String name = fluid.getName();
            if (name.equals("lava") || name.equals("water")) {
                ret = "Minecraft";
            } else {
                ret = dictionary.get(ItemStackHelper.NBT.getFluid(stack).getStill().toString().split(":")[0]);
            }
        } else {
            String name = stack.getItem().getRegistryName().toString();
            name = name.substring(0, name.indexOf(":"));
            if (name.equals("minecraft")) {
                ret = "Minecraft";
            } else {
                ret = dictionary.get(name);
            }
        }
        return ret;
    }

    public static int scaleOfInt(int x) {
        for (int i = 0;; i++)
            if (x <= scaleTable[i])
                return i + 1;
    }
}
