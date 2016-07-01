package pers.towdium.just_enough_calculation.util;

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
    public static String format(String translateKey, Object... parameters) {
        String buffer = I18n.format(translateKey, parameters);
        buffer = StringEscapeUtils.unescapeJava(buffer);
        return buffer.replace("\t", "    ");
    }
}
