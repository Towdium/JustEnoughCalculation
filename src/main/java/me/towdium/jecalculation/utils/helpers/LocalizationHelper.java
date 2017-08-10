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

    public static Pair<String, Boolean> format(String translateKey, Object... parameters) {
        Pair<String, Boolean> ret = new Pair<>(null, null);
        String buffer = I18n.format(translateKey, parameters);
        ret.two = !buffer.equals(translateKey);
        buffer = StringEscapeUtils.unescapeJava(buffer);
        ret.one = buffer.replace("\t", "    ");
        return ret;
    }

    // localize key: prefix.className(superClassName).translateKey
    // return: value, if found
    public static Pair<String, Boolean> localization(Class c, String prefix, String translateKey, Object... parameters) {
        StringBuilder builder = new StringBuilder(c.getName());
        builder.delete(0, builder.lastIndexOf(".") + 1).setCharAt(0, Character.toLowerCase(builder.charAt(0)));
        builder.insert(0, prefix).append('.').append(translateKey);
        String key = builder.toString();
        Pair<String, Boolean> ret = LocalizationHelper.format(key, parameters);
        if (!ret.two && !c.equals(Object.class)) {
            ret = localization(c.getSuperclass(), prefix, translateKey, parameters);
        }
        if (ret.two) {
            return ret;
        } else {
            ret.one = key;
            return ret;
        }
    }
}
