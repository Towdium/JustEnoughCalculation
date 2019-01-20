package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.structure.RecordMath.Operator;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Objects;

import static me.towdium.jecalculation.data.structure.RecordMath.DOT_NONE;


/**
 * Author: Towdium
 * Date: 19-1-16
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WLcd implements IWidget {
    static HashMap<Character, boolean[]> PATTERN = new HashMap<>();

    public int yPos;
    public String text = "";
    public int dot = DOT_NONE;
    static NumberFormat format = new DecimalFormat("0.0E0");
    public Operator operator = Operator.EQUALS;

    static {
        PATTERN.put('0', new boolean[]{true, true, true, true, true, true, false});
        PATTERN.put('1', new boolean[]{false, true, true, false, false, false, false});
        PATTERN.put('2', new boolean[]{true, true, false, true, true, false, true});
        PATTERN.put('3', new boolean[]{true, true, true, true, false, false, true});
        PATTERN.put('4', new boolean[]{false, true, true, false, false, true, true});
        PATTERN.put('5', new boolean[]{true, false, true, true, false, true, true});
        PATTERN.put('6', new boolean[]{true, false, true, true, true, true, true});
        PATTERN.put('7', new boolean[]{true, true, true, false, false, false, false});
        PATTERN.put('8', new boolean[]{true, true, true, true, true, true, true});
        PATTERN.put('9', new boolean[]{true, true, true, true, false, true, true});
        PATTERN.put('E', new boolean[]{true, false, false, true, true, true, true});
        PATTERN.put('o', new boolean[]{false, false, true, true, true, false, true});
        PATTERN.put('r', new boolean[]{false, false, false, false, true, false, true});
        PATTERN.put('-', new boolean[]{false, false, false, false, false, false, true});
        PATTERN.put(' ', new boolean[]{false, false, false, false, false, false, false});
        //format.setMinimumIntegerDigits(0);
    }

    public WLcd(int yPos) {
        this.yPos = yPos;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        drawBackground(gui);
        drawStr(gui, text);
        if (dot != DOT_NONE) drawDot(gui, 6 - dot);
        drawOperator(gui, operator);
    }

    protected void drawBackground(JecaGui gui) {
        gui.drawResourceContinuous(Resource.WGT_LCD_BG, 7, yPos, 162, 50, 8);
        for (int i = 0; i < 7; i++) {
            gui.drawResource(Resource.WGT_LCD_UL_N, 16 + i * 21, yPos + 14);
            gui.drawResource(Resource.WGT_LCD_UR_N, 30 + i * 21, yPos + 14);
            gui.drawResource(Resource.WGT_LCD_LL_N, 16 + i * 21, yPos + 28);
            gui.drawResource(Resource.WGT_LCD_LR_N, 30 + i * 21, yPos + 28);
            gui.drawResource(Resource.WGT_LCD_H_N, 19 + i * 21, yPos + 14);
            gui.drawResource(Resource.WGT_LCD_H_N, 19 + i * 21, yPos + 27);
            gui.drawResource(Resource.WGT_LCD_H_N, 19 + i * 21, yPos + 40);
            gui.drawResource(Resource.WGT_LCD_DO_N, 33 + i * 21, yPos + 40);
        }
        gui.drawResource(Resource.WGT_LCD_P_N, 132, yPos + 6);
        gui.drawResource(Resource.WGT_LCD_M_N, 139, yPos + 6);
        gui.drawResource(Resource.WGT_LCD_T_N, 146, yPos + 6);
        gui.drawResource(Resource.WGT_LCD_D_N, 153, yPos + 6);
    }

    protected void drawChar(JecaGui gui, char ch, int index) {
        boolean[] pattern = PATTERN.get(ch);
        Objects.requireNonNull(pattern, "Unsupported char: " + ch + ".");
        if (pattern[0]) gui.drawResource(Resource.WGT_LCD_H_F, 19 + index * 21, yPos + 14);
        if (pattern[1]) gui.drawResource(Resource.WGT_LCD_UR_F, 30 + index * 21, yPos + 14);
        if (pattern[2]) gui.drawResource(Resource.WGT_LCD_LR_F, 30 + index * 21, yPos + 28);
        if (pattern[3]) gui.drawResource(Resource.WGT_LCD_H_F, 19 + index * 21, yPos + 40);
        if (pattern[4]) gui.drawResource(Resource.WGT_LCD_LL_F, 16 + index * 21, yPos + 28);
        if (pattern[5]) gui.drawResource(Resource.WGT_LCD_UL_F, 16 + index * 21, yPos + 14);
        if (pattern[6]) gui.drawResource(Resource.WGT_LCD_H_F, 19 + index * 21, yPos + 27);
    }

    protected void drawStr(JecaGui gui, String str) {
        int offset = 7 - str.length();
        for (int i = offset; i < 7; i++) drawChar(gui, str.charAt(i - offset), i);
    }

    protected void drawDot(JecaGui gui, int index) {
        gui.drawResource(Resource.WGT_LCD_DO_F, 33 + index * 21, yPos + 40);
    }

    protected void drawOperator(JecaGui gui, Operator operator) {
        switch (operator) {
            case PLUS: gui.drawResource(Resource.WGT_LCD_P_F, 132, yPos + 6);
                break;
            case MINUS: gui.drawResource(Resource.WGT_LCD_M_F, 139, yPos + 6);
                break;
            case TIMES: gui.drawResource(Resource.WGT_LCD_T_F, 146, yPos + 6);
                break;
            case DIVIDE: gui.drawResource(Resource.WGT_LCD_D_F, 153, yPos + 6);
                break;
        }
    }

    public void print(BigDecimal num) {
        String s = num.stripTrailingZeros().toPlainString();
        double f = num.floatValue();
        int len = f > 0 ? 7 : 6;
        int sca = num.unscaledValue().abs().toString().length() - num.scale() - 1;
        if (Math.abs(sca) > len) {
            String scale = String.valueOf(sca);
            len -= scale.length() + 2;
            format.setMaximumFractionDigits(len);
            s = format.format(num);
        }
        int d = s.indexOf('.');
        if (d >= 0 && d <= 7) {
            text = s.substring(0, d) + s.substring(d + 1, Math.min(s.length(), 8));
            dot = text.length() - d;
        } else {
            text = s;
            dot = DOT_NONE;
        }
    }
}
