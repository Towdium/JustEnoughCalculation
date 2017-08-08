package me.towdium.jecalculation.event;

import me.towdium.jecalculation.gui.guis.GuiPickerFluid;
import me.towdium.jecalculation.util.Utilities;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Towdium
 * Date:   2016/9/1.
 */
public class TooltipEventHandler {
    static Pattern pattern = Pattern.compile("Just Enough Calculation");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() != null && event.getEntityPlayer().openContainer instanceof GuiPickerFluid.ContainerPickerFluid) {
            Iterator<String> i = event.getToolTip().iterator();
            String str;
            Matcher matcher = null;
            boolean flag = false;
            while (i.hasNext()) {
                str = i.next();
                matcher = pattern.matcher(str);
                if (matcher.find()) {
                    i.remove();
                    flag = true;
                }
            }
            if (flag) {
                event.getToolTip().add(matcher.replaceAll(Utilities.getModName(event.getItemStack())));
            }
        }
    }
}
