package me.towdium.jecalculation.utils;

import java.lang.reflect.Field;

/**
 * Author: Towdium
 * Date:   2016/6/25.
 */
public class ReflectionHelper {
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
            StringBuilder buffer =
                    new StringBuilder("Field not found in class " + o.getClass().getCanonicalName() + ":");
            for (String s : names) {
                buffer.append(" ");
                buffer.append(s);
            }
            throw new NoSuchFieldError(buffer.toString());
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

    public static <T, C> T get(C o, String name) {
        T res;
        try {
            Field field = o.getClass().getDeclaredField(name);
            field.setAccessible(true);
            res = (T) field.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return res;
    }

    public static <T, C> void set(C o, String name, T value) {
        try {
            Field field = o.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(o, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
