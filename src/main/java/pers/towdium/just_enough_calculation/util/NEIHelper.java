package pers.towdium.just_enough_calculation.util;

import codechicken.nei.guihook.GuiContainerManager;
import net.minecraft.client.renderer.RenderItem;

/**
 * Author: Towdium
 * Date:   2016/9/21.
 */
public class NEIHelper {
    public static RenderItem getRenderer() {
        return GuiContainerManager.drawItems;
    }

    public static void setRenderer(RenderItem renderer) {
        GuiContainerManager.drawItems = renderer;
    }
}
