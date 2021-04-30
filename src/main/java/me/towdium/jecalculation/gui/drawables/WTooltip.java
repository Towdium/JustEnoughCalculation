package me.towdium.jecalculation.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.IWidget;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.Utilities;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-16.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public abstract class WTooltip implements IWidget {
    @Nullable
    public String name;
    protected Utilities.Timer timer = new Utilities.Timer();

    public WTooltip(@Nullable String name) {
        this.name = name;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        if (name != null) {
            timer.setState(mouseIn(xMouse, yMouse));
            if (timer.getTime() > 500) {
                String str = getSuffix().stream().map(s -> Utilities.I18n.search(String.join(".", "gui", name, s)))
                                        .filter(p -> p.two).findFirst().map(p -> p.one)
                                        .orElse(JecaGui.ALWAYS_TOOLTIP ? String.join(".", "gui", name, getSuffix().get(0)) : null);
                if (str != null) gui.drawTooltip(xMouse, yMouse, str);
            }
        }
    }

    protected List<String> getSuffix() {
        return Collections.singletonList("tooltip");
    }

    public abstract boolean mouseIn(int xMouse, int yMouse);
}
