package me.towdium.jecalculation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.StringEscapeUtils;

@SideOnly(Side.CLIENT)
public class JecaTranslator {
    public static String format(String translateKey, Object... parameters){
        String buffer = I18n.format(translateKey, parameters);
        buffer = StringEscapeUtils.unescapeJava(buffer);
        return buffer.replace("\t", "    ");
    }
}
