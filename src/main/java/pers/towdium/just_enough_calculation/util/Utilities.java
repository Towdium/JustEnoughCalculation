package pers.towdium.just_enough_calculation.util;

import com.google.common.primitives.Ints;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import pers.towdium.just_enough_calculation.util.function.TriFunction;

import java.awt.*;
import java.lang.reflect.Field;

/**
 * Author: Towdium
 * Date:   2016/6/25.
 */
public class Utilities {

    // FOR STRING FORMATTING

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

    public static String cutFloat(float f, int size) {
        TriFunction<Float, Integer, Integer, String> form = (fl, len, max) -> {
            int scale = len - 1 - String.valueOf(fl.intValue()).length();
            return String.format("%." + (scale > max ? max : scale) + 'f', fl);
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

    // FOR MODEL CALCULATING

    public static int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v) {
        return new int[]{
                Float.floatToRawIntBits(x),
                Float.floatToRawIntBits(y),
                Float.floatToRawIntBits(z),
                color,
                Float.floatToRawIntBits(texture.getInterpolatedU(u)),
                Float.floatToRawIntBits(texture.getInterpolatedV(v)),
                0
        };
    }

    public static BakedQuad createBakedQuadForFace(float centreLR, float width, float centreUD, float height, float forwardDisplacement,
                                                   int itemRenderLayer, TextureAtlasSprite texture, EnumFacing face) {
        float x1, x2, x3, x4;
        float y1, y2, y3, y4;
        float z1, z2, z3, z4;
        final float CUBE_MIN = 0.0F;
        final float CUBE_MAX = 1.0F;
        final float u1 = (centreLR - width / 2.0f) * 16.0f;
        final float u2 = (centreLR + width / 2.0f) * 16.0f;
        final float v1 = (centreUD - height / 2.0f) * 16.0f;
        final float v2 = (centreUD + height / 2.0f) * 16.0f;

        switch (face) {
            case UP: {
                x1 = x2 = centreLR + width / 2.0F;
                x3 = x4 = centreLR - width / 2.0F;
                z1 = z4 = centreUD + height / 2.0F;
                z2 = z3 = centreUD - height / 2.0F;
                y1 = y2 = y3 = y4 = CUBE_MAX + forwardDisplacement;
                break;
            }
            case DOWN: {
                x1 = x2 = centreLR + width / 2.0F;
                x3 = x4 = centreLR - width / 2.0F;
                z1 = z4 = centreUD - height / 2.0F;
                z2 = z3 = centreUD + height / 2.0F;
                y1 = y2 = y3 = y4 = CUBE_MIN - forwardDisplacement;
                break;
            }
            case WEST: {
                z1 = z2 = centreLR + width / 2.0F;
                z3 = z4 = centreLR - width / 2.0F;
                y1 = y4 = centreUD - height / 2.0F;
                y2 = y3 = centreUD + height / 2.0F;
                x1 = x2 = x3 = x4 = CUBE_MIN - forwardDisplacement;
                break;
            }
            case EAST: {
                z1 = z2 = centreLR - width / 2.0F;
                z3 = z4 = centreLR + width / 2.0F;
                y1 = y4 = centreUD - height / 2.0F;
                y2 = y3 = centreUD + height / 2.0F;
                x1 = x2 = x3 = x4 = CUBE_MAX + forwardDisplacement;
                break;
            }
            case NORTH: {
                x1 = x2 = centreLR - width / 2.0F;
                x3 = x4 = centreLR + width / 2.0F;
                y1 = y4 = centreUD - height / 2.0F;
                y2 = y3 = centreUD + height / 2.0F;
                z1 = z2 = z3 = z4 = CUBE_MIN - forwardDisplacement;
                break;
            }
            case SOUTH: {
                x1 = x2 = centreLR + width / 2.0F;
                x3 = x4 = centreLR - width / 2.0F;
                y1 = y4 = centreUD - height / 2.0F;
                y2 = y3 = centreUD + height / 2.0F;
                z1 = z2 = z3 = z4 = CUBE_MAX + forwardDisplacement;
                break;
            }
            default: {
                assert false : "Unexpected facing in createBakedQuadForFace:" + face;
                return null;
            }
        }

        return new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), texture, u2, v2),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), texture, u2, v1),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, u1, v1),
                vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), texture, u1, v2)),
                itemRenderLayer, face, texture, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM);
    }

    // REFLECTION

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
}
