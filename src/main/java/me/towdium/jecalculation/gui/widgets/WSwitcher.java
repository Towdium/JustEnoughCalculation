package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.Utilities.Circulator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.towdium.jecalculation.gui.JecaGui.COLOR_GUI_GREY;
import static me.towdium.jecalculation.gui.JecaGui.Font.SHADOW;
import static me.towdium.jecalculation.gui.Resource.WGT_ARR_L;
import static me.towdium.jecalculation.gui.Resource.WGT_ARR_R;

/**
 * Author: towdium
 * Date:   17-8-19.
 * Widget to select page with button for left and right
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WSwitcher extends WContainer {
    public static final int SIZE = 13;
    public ListenerAction<? super WSwitcher> listener;
    protected int xPos, xSize, yPos;
    protected WButton left, right;
    protected WRectangle wRect;
    protected WText wText;
    protected List<String> keys;
    protected Circulator index;
    protected String temp;

    public WSwitcher(int xPos, int yPos, int xSize, int total) {
        this(xPos, yPos, xSize, IntStream.rangeClosed(1, total)
                .mapToObj(i -> i + "/" + total).collect(Collectors.toList()));
    }

    public WSwitcher(int xPos, int yPos, int xSize, List<String> keys) {
        this.xPos = xPos;
        this.xSize = xSize;
        this.yPos = yPos;
        this.keys = keys;
        left = new WButtonIcon(xPos, yPos, SIZE, SIZE, WGT_ARR_L).setListener(i -> {
            if (temp == null) move(false);
            else setTemp(null);
        });
        right = new WButtonIcon(xPos + xSize - SIZE, yPos, SIZE, SIZE, WGT_ARR_R).setListener(i -> {
            if (temp == null) move(true);
            else setTemp(null);
        });
        wRect = new WRectangle(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, COLOR_GUI_GREY);
        wText = new WText(xPos + SIZE, yPos + 2, xSize - 2 * SIZE, SHADOW, "", true);
        index = new Circulator(keys.size());
        refresh();
        addAll(left, right, wRect, wText);
    }

    protected void move(boolean forward) {
        if (temp == null) {
            if (forward) index.move(1);
            else index.move(-1);
        } else temp = null;
        notifyLsnr();
        refresh();
    }

    public WSwitcher setListener(ListenerAction<? super WSwitcher> listener) {
        this.listener = listener;
        return this;
    }

    public void setTemp(@Nullable String temp) {
        this.temp = temp;
        notifyLsnr();
        refresh();
    }

    public int getIndex() {
        return temp == null ? index.current() : -1;
    }

    public void setIndex(int i) {
        if (temp != null) temp = null;
        index.set(i);
        refresh();
    }

    public List<String> getTexts() {
        return keys;
    }

    public String getText() {
        return temp == null ? keys.get(index.current()) : temp;
    }

    public void refresh() {
        boolean b = keys.size() < (temp == null ? 2 : 1);
        left.setDisabled(b);
        right.setDisabled(b);
        wText.key = temp == null ? (keys.isEmpty() ? "" : keys.get(index.current())) : temp;
    }

    public void notifyLsnr() {
        if (listener != null) listener.invoke(this);
    }
}
