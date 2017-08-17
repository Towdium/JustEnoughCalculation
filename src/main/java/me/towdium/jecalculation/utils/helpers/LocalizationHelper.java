package me.towdium.jecalculation.utils.helpers;

import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Author:  Towdium
 * Created: 2016/6/22.
 */
@SideOnly(Side.CLIENT)
public class LocalizationHelper {

    public static Pair<String, Boolean> search(String translateKey, Object... parameters) {
        Pair<String, Boolean> ret = new Pair<>(null, null);
        String buffer = I18n.format(translateKey, parameters);
        ret.two = !buffer.equals(translateKey);
        buffer = StringEscapeUtils.unescapeJava(buffer);
        ret.one = buffer.replace("\t", "    ");
        return ret;
    }

    /**
     * @param c            the (super) class name will be used after prefix translation key
     * @param prefix       the prefix of translation key
     * @param translateKey the body of translation key
     * @param parameters   format parameters
     * @return the localized value
     * The localized key is "prefix.className(superClassName).translateKey".
     * It will search all the super classes.
     */
    public static Pair<String, Boolean> search(Class c, String prefix, String translateKey, Object... parameters) {
        StringBuilder builder = new StringBuilder(c.getName());
        builder.delete(0, builder.lastIndexOf(".") + 1).setCharAt(0, Character.toLowerCase(builder.charAt(0)));
        builder.insert(0, prefix).append('.').append(translateKey);  // TODO simplify and change underscore
        String key = builder.toString();
        Pair<String, Boolean> ret = LocalizationHelper.search(key, parameters);
        if (ret.two || c.equals(Object.class)) return ret;
        else {
            ret = search(c.getSuperclass(), prefix, translateKey, parameters);
            if (ret.two) return ret;
            else return ret.setOne(key);
        }
    }

    public static String format(String translateKey, Object... parameters) {
        return search(translateKey, parameters).one;
    }

    public static String format(Class c, String prefix, String translateKey, Object... parameters) {
        return search(c, prefix, translateKey, parameters).one;
    }
}
