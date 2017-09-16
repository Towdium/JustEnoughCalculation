package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
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
public class DScroll implements IDrawable {
    public static final int SIZE = 13;

    protected int xPos, xSize, yPos;
    protected DButton left, right;
    protected DRectangle wRect;
    protected DText dText;
    protected List<String> keys;
    protected Circulator index;

    public DScroll(int xPos, int yPos, int xSize, int total) {
        this(xPos, yPos, xSize, IntStream.rangeClosed(1, total)
                .mapToObj(i -> i + "/" + total).collect(Collectors.toList()));
    }

    public DScroll(int xPos, int yPos, int xSize, List<String> keys) {
        this.xPos = xPos;
        this.xSize = xSize;
        this.yPos = yPos;
        this.keys = keys;
        left = new DButtonIcon(xPos, yPos, SIZE, SIZE, Resource.WGT_ARR_L_N, Resource.WGT_ARR_L_F)
                .setListenerLeft(() -> index.prev());
        right = new DButtonIcon(xPos + xSize - SIZE, yPos, SIZE, SIZE, Resource.WGT_ARR_R_N, Resource.WGT_ARR_R_F)
                .setListenerLeft(() -> index.next());
        wRect = new DRectangle(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, JecGui.COLOR_GREY);
        dText = new DText(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, JecGui.Font.DEFAULT_SHADOW,
                () -> keys.get(index.index()));
        index = new Circulator(keys.size());
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        Stream.of(left, right, wRect, dText).forEach(w -> w.onDraw(gui, xMouse, yMouse));
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
