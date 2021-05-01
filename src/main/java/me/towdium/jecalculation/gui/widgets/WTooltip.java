package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.join;

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
        if (name != null)
            timer.setState(mouseIn(xMouse, yMouse));
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        if (timer.getTime() > 500) {
            List<Pair<String, Boolean>> suffix = getSuffix().stream().map(s -> Utilities.I18n
                    .search(s.isEmpty() ? join(".", "gui", name) : join(".", "gui", name, s)))
                                                            .collect(Collectors.toList());
            String str = suffix.stream().filter(p -> p.two).findFirst().map(p -> p.one)
                               .orElse(JecaGui.ALWAYS_TOOLTIP ? suffix.get(0).one : null);
            if (str != null)
                tooltip.add(str);
        }
        return false;
    }

    protected List<String> getSuffix() {
        return Collections.singletonList("");
    }

    public abstract boolean mouseIn(int xMouse, int yMouse);
}
