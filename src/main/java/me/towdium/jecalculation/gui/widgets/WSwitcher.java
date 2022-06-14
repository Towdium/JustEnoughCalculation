package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.utils.Utilities.Circulator;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.towdium.jecalculation.gui.JecaGui.COLOR_GUI_GREY;
import static me.towdium.jecalculation.gui.JecaGui.FontType.SHADOW;
import static me.towdium.jecalculation.gui.Resource.WGT_ARR_L;
import static me.towdium.jecalculation.gui.Resource.WGT_ARR_R;

/**
 * Author: towdium
 * Date:   17-8-19.
 * Widget to select page with button for left and right
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
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
            if (temp == null) move(-1);
            else setText(null);
        });
        right = new WButtonIcon(xPos + xSize - SIZE, yPos, SIZE, SIZE, WGT_ARR_R).setListener(i -> {
            if (temp == null) move(1);
            else setText(null);
        });
        wRect = new WRectangle(xPos + SIZE, yPos, xSize - 2 * SIZE, SIZE, COLOR_GUI_GREY);
        wText = new WText(xPos + SIZE, yPos + 2, xSize - 2 * SIZE, SHADOW, "", true);
        index = new Circulator(keys.size());
        refresh();
        add(left, right, wRect, wText);
    }

    public WSwitcher setDisabled(boolean b) {
        left.setDisabled(b);
        right.setDisabled(b);
        return this;
    }

    public void move(int diff) {
        if (diff == 0) return;
        if (temp != null) {
            temp = null;
            if (diff > 0) diff -= 1;
            else diff += 1;
        }
        index.move(diff);
        notifyLsnr();
        refresh();
    }

    public WSwitcher setListener(ListenerAction<? super WSwitcher> listener) {
        this.listener = listener;
        return this;
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

    public WSwitcher setText(@Nullable String s) {
        int i = keys.indexOf(s);
        if (i != -1) setIndex(i);
        else temp = s;
        notifyLsnr();
        refresh();
        return this;
    }

    public void notifyLsnr() {
        if (listener != null) listener.invoke(this);
    }
}
