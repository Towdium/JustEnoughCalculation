package me.towdium.jecalculation.utils;

import java.lang.reflect.Field;

/**
 * Author: Towdium
 * Date:   2016/6/25.
 */
public class ReflectionHelper {
    @SuppressWarnings("unchecked")
    public static <T, C> T getField(C o, String... names){
        Field field = null;
        boolean flag = false;
        for(String name : names){
            try {
                field = o.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                continue;
            }
            flag = true;
            break;
        }
        if(!flag){
            StringBuilder buffer = new StringBuilder(
                    "Field not found in class " + o.getClass().getCanonicalName() + ":");
            for(String s : names){
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
}
