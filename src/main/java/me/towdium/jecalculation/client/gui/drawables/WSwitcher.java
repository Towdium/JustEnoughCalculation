package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IWidget;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.utils.Utilities.Circulator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Author: towdium
 * Date:   17-8-19.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WSwitcher implements IWidget {
    public static final int SIZE = 13;

    protected int xPos, xSize, yPos;
    protected WButton left, right;
    protected WRectangle wRect;
    protected WText wText;
    protected List<String> keys;
    protected Circulator index;
    public Consumer<Integer> listener;

    public WSwitcher(int xPos, int yPos, int xSize, int total) {
        this(xPos, yPos, xSize, IntStream.rangeClosed(1, total)
                .mapToObj(i -> i + "/" + total).collect(Collectors.toList()));
    }

    public WSwitcher(int xPos, int yPos, int xSize, List<String> keys) {
        this.xPos = xPos;
        this.xSize = xSize;
        this.yPos = yPos;
        this.keys = keys;
        left = new WButtonIcon(xPos, yPos, SIZE, SIZE, Resource.WGT_ARR_L_N, Resource.WGT_ARR_L_F,
                Resource.WGT_ARR_L_D).setListenerLeft(() -> move(true));
        right = new WButtonIcon(xPos + xSize - SIZE, yPos, SIZE, SIZE, Resource.WGT_ARR_R_N, Resource.WGT_ARR_R_F,
                Resource.WGT_ARR_R_D).setListenerLeft(() -> move(false));
        wRect = new WRectangle(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, JecGui.COLOR_GUI_GREY);
        wText = new WText(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, JecGui.Font.DEFAULT_SHADOW,
                () -> keys.get(index.index()));
        index = new Circulator(keys.size());
        if (keys.size() == 1) {
            left.setDisabled(true);
            right.setDisabled(true);
        }
    }

    protected void move(boolean forward) {
        if (forward) index.next();
        else index.prev();

        if (listener != null) listener.accept(index.index());
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        Stream.of(left, right, wRect, wText).forEach(w -> w.onDraw(gui, xMouse, yMouse));
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        return Stream.of(left, right).anyMatch(w -> w.onClicked(gui, xMouse, yMouse, button));
    }

    @Override
    public boolean onKey(JecGui gui, char ch, int code) {
        return Stream.of(left, right).anyMatch(w -> w.onKey(gui, ch, code));
    }

    public WSwitcher setListener(Consumer<Integer> listener) {
        this.listener = listener;
        return this;
    }
}
