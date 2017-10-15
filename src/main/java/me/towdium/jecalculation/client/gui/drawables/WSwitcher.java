package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.utils.Utilities.Circulator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Author: towdium
 * Date:   17-8-19.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WSwitcher extends WContainer {
    public static final int SIZE = 13;

    protected int xPos, xSize, yPos;
    protected WButton left, right;
    protected WRectangle wRect;
    protected WText wText;
    protected List<String> keys;
    protected Circulator index;
    @Nullable
    protected String temp;
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
                Resource.WGT_ARR_L_D).setListenerLeft(() -> move(false));
        right = new WButtonIcon(xPos + xSize - SIZE, yPos, SIZE, SIZE, Resource.WGT_ARR_R_N, Resource.WGT_ARR_R_F,
                Resource.WGT_ARR_R_D).setListenerLeft(() -> move(true));
        wRect = new WRectangle(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, JecGui.COLOR_GUI_GREY);
        wText = new WText(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, JecGui.Font.DEFAULT_SHADOW,
                () -> temp == null ? keys.get(index.current()) : temp);
        index = new Circulator(keys.size());
        if (keys.size() == 1) {
            left.setDisabled(true);
            right.setDisabled(true);
        }
        addAll(left, right, wRect, wText);
    }

    protected void move(boolean forward) {
        if (temp == null) {
            if (forward) index.move(1);
            else index.move(-1);
        } else temp = null;

        if (listener != null) listener.accept(getIndex());
    }

    public WSwitcher setListener(Consumer<Integer> listener) {
        this.listener = listener;
        return this;
    }

    public void setTemp(@Nullable String temp) {
        this.temp = temp;
        if (listener != null) listener.accept(getIndex());
    }

    public int getIndex() {
        return temp == null ? index.current() : -1;
    }

    public String getText() {
        return temp == null ? keys.get(index.current()) : temp;
    }
}
