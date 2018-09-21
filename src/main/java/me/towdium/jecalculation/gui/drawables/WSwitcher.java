package me.towdium.jecalculation.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities.Circulator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public Runnable listener;
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
        left = new WButtonIcon(xPos, yPos, SIZE, SIZE, Resource.WGT_ARR_L).setLsnrLeft(() -> {
            if (temp == null) move(false);
            else setTemp(null);
        });
        right = new WButtonIcon(xPos + xSize - SIZE, yPos, SIZE, SIZE, Resource.WGT_ARR_R).setLsnrLeft(() -> {
            if (temp == null) move(true);
            else setTemp(null);
        });
        wRect = new WRectangle(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, JecaGui.COLOR_GUI_GREY);
        wText = new WText(xPos + SIZE, yPos + 2, xSize - 2 * SIZE, JecaGui.Font.SHADOW, "", true);
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
    }

    public WSwitcher setListener(Runnable listener) {
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
        if (listener != null) listener.run();
    }
}
