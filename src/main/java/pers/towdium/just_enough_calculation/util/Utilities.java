package pers.towdium.just_enough_calculation.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import pers.towdium.just_enough_calculation.util.function.TriFunction;

import java.lang.reflect.Field;

/**
 * Author: Towdium
 * Date:   2016/6/25.
 */
public class Utilities {

    // FOR STRING FORMATTING

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

    public static String cutString(String s, int length, FontRenderer fontRenderer) {
        return fontRenderer.getStringWidth(s) <= length ? s : fontRenderer.trimStringToWidth(s, length - 6) + "...";
    }

    // REFLECTION

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

    public static Object getField(Class c, Object o, String... names) throws ReflectiveOperationException {
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
            throw new NoSuchFieldException(s.substring(0, s.length() - 1) + " not found in class " + c.getName());
        }
        f.setAccessible(true);
        o = f.get(o);
        return o;
    }

    // CIRCULATE STRUCTURE

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

    // GUI

    public static void openGui(GuiScreen gui) {
        Minecraft.getMinecraft().displayGuiScreen(gui);
        /*if (gui instanceof JECGuiContainer) {
            if (gui instanceof GuiCalculator) {
                ((GuiCalculator) gui).updateLayout();
            }
            ((JECGuiContainer) gui).updateLayout();
        }*/
    }
}
