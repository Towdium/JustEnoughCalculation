package me.towdium.jecalculation.client.gui.widget.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.resource.Resource;
import me.towdium.jecalculation.client.gui.widget.Widget;
import me.towdium.jecalculation.utils.Utilities.Circulator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Author: towdium
 * Date:   17-8-19.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WPager extends Widget.Advanced {
    public static final int SIZE = 13;

    protected int xPos, xSize, yPos;
    protected WButtonIcon left, right;
    protected WRectangle wRect;
    protected WText wText;
    protected List<String> keys;
    protected Circulator index;

    public WPager(int xPos, int yPos, int xSize, int total) {
        this(xPos, yPos, xSize, IntStream.rangeClosed(1, total)
                .mapToObj(i -> i + "/" + total).collect(Collectors.toList()));
    }

    public WPager(int xPos, int yPos, int xSize, List<String> keys) {
        this.xPos = xPos;
        this.xSize = xSize;
        this.yPos = yPos;
        this.keys = keys;
        left = new WButtonIcon(xPos, yPos, SIZE, SIZE, Resource.WGT_ARR_L_N, Resource.WGT_ARR_L_F)
                .setListenerLeft(() -> index.prev());
        right = new WButtonIcon(xPos + xSize - SIZE, yPos, SIZE, SIZE,
                Resource.WGT_ARR_R_N, Resource.WGT_ARR_R_F)
                .setListenerLeft(() -> index.next());
        wRect = new WRectangle(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, JecGui.COLOR_GREY);
        wText = new WText(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, JecGui.Font.DEFAULT_SHADOW,
                () -> keys.get(index.index()));
        index = new Circulator(keys.size());
    }

    @Override
    public void onGuiInit(JecGui gui) {
        Stream.of(left, right).forEach(w -> w.onGuiInit(gui));
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        Stream.of(left, right, wRect, wText).forEach(w -> w.onDraw(gui, xMouse, yMouse));
    }

    @Override
    public void onRemoved(JecGui gui) {
        Stream.of(left, right).forEach(w -> w.onRemoved(gui));
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        return Stream.of(left, right).anyMatch(w -> w.onClicked(gui, xMouse, yMouse, button));
    }

    @Override
    public boolean onKey(JecGui gui, char ch, int code) {
        return Stream.of(left, right).anyMatch(w -> w.onKey(gui, ch, code));
    }
}
