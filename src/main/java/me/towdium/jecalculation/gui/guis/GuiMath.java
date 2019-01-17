package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.IllegalPositionException;
import me.towdium.jecalculation.utils.Utilities;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Author: Towdium
 * Date: 19-1-3
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuiMath extends WContainer implements IGui {
    WLcd lcd = new WLcd(7);
    LinkedList<BigDecimal> numbers = new LinkedList<>();
    BigDecimal last = BigDecimal.ZERO;
    int dot = -1;
    boolean sign = true;
    Operator operator = Operator.EQUALS;
    State state = State.INPUT;
    static MathContext context = new MathContext(7, RoundingMode.HALF_UP);

    static final int DOT_NONE = -1;

    enum State {INPUT, OUTPUT, ERROR}

    enum Operator {
        PLUS, MINUS, TIMES, DIVIDE, EQUALS;

        public WLcd.Operator map() {
            switch (this) {
                case PLUS: return WLcd.Operator.PLUS;
                case MINUS: return WLcd.Operator.MINUS;
                case TIMES: return WLcd.Operator.TIMES;
                case DIVIDE: return WLcd.Operator.DIVIDE;
                case EQUALS: return WLcd.Operator.NONE;
                default: throw new IllegalPositionException();
            }
        }

        public BigDecimal operate(BigDecimal a, BigDecimal b) {
            switch (this) {
                case PLUS: return a.add(b);
                case MINUS: return a.subtract(b);
                case TIMES: return a.multiply(b);
                case DIVIDE: return a.divide(b, context);
                case EQUALS: return b;
                default: throw new IllegalPositionException();
            }
        }
    }

    public GuiMath() {
        add(new WPanel(), lcd);
        add(new WButtonText(7, 67, 28, 20, "7").setListener(i -> append(7)).setKeyBind(Keyboard.KEY_7, Keyboard.KEY_NUMPAD7));
        add(new WButtonText(39, 67, 28, 20, "8").setListener(i -> append(8)).setKeyBind(Keyboard.KEY_8, Keyboard.KEY_NUMPAD8));
        add(new WButtonText(71, 67, 28, 20, "9").setListener(i -> append(9)).setKeyBind(Keyboard.KEY_9, Keyboard.KEY_NUMPAD9));
        add(new WButtonText(7, 91, 28, 20, "4").setListener(i -> append(4)).setKeyBind(Keyboard.KEY_4, Keyboard.KEY_NUMPAD4));
        add(new WButtonText(39, 91, 28, 20, "5").setListener(i -> append(5)).setKeyBind(Keyboard.KEY_5, Keyboard.KEY_NUMPAD5));
        add(new WButtonText(71, 91, 28, 20, "6").setListener(i -> append(6)).setKeyBind(Keyboard.KEY_6, Keyboard.KEY_NUMPAD6));
        add(new WButtonText(7, 115, 28, 20, "1").setListener(i -> append(1)).setKeyBind(Keyboard.KEY_1, Keyboard.KEY_NUMPAD1));
        add(new WButtonText(39, 115, 28, 20, "2").setListener(i -> append(2)).setKeyBind(Keyboard.KEY_2, Keyboard.KEY_NUMPAD2));
        add(new WButtonText(71, 115, 28, 20, "3").setListener(i -> append(3)).setKeyBind(Keyboard.KEY_3, Keyboard.KEY_NUMPAD3));
        add(new WButtonText(7, 139, 28, 20, "0").setListener(i -> append(0)).setKeyBind(Keyboard.KEY_0, Keyboard.KEY_NUMPAD0));
        add(new WButtonText(39, 139, 28, 20, "00").setListener(i -> {
            append(0);
            append(0);
        }));
        add(new WButtonText(71, 139, 28, 20, ".").setListener(i -> dot()).setKeyBind(Keyboard.KEY_PERIOD, Keyboard.KEY_DECIMAL));
        add(new WButtonIcon(109, 67, 28, 20, Resource.WGT_ARR_L).setListener(i -> remove()).setKeyBind(Keyboard.KEY_BACK));
        add(new WButtonText(141, 67, 28, 20, "+").setListener(i -> operate(Operator.PLUS)).setKeyBind(Keyboard.KEY_EQUALS, Keyboard.KEY_ADD));
        add(new WButtonText(109, 91, 28, 20, "C").setListener(i -> reset()).setKeyBind(Keyboard.KEY_DELETE));
        add(new WButtonText(141, 91, 28, 20, "-").setListener(i -> operate(Operator.MINUS)).setKeyBind(Keyboard.KEY_SUBTRACT, Keyboard.KEY_MINUS));
        add(new WButtonText(109, 115, 28, 44, "=").setListener(i -> operate(Operator.EQUALS)).setKeyBind(Keyboard.KEY_RETURN, Keyboard.KEY_NUMPADENTER, Keyboard.KEY_NUMPADEQUALS));
        add(new WButtonText(141, 115, 28, 20, "x").setListener(i -> operate(Operator.TIMES)).setKeyBind(Keyboard.KEY_MULTIPLY));
        add(new WButtonText(141, 139, 28, 20, "/").setListener(i -> operate(Operator.DIVIDE)).setKeyBind(Keyboard.KEY_SLASH, Keyboard.KEY_DIVIDE));
        add(new WLine(61), new WLine(103, 61, 98, false), new WLine(103, 61, 2, true), new WRectangle(103, 61, 2, 1, 0xFF373737));
        print();
    }

    private void print() {
        if (state == State.INPUT) {
            String s = numbers.isEmpty() ? "0" : numbers.stream().map(Object::toString).collect(Collectors.joining());
            if (!sign) s = '-' + Utilities.repeat(" ", 6 - s.length()) + s;
            lcd.text = s;
            lcd.dot = dot;
        } else if (state == State.ERROR) {
            lcd.text = "Error";
            lcd.dot = DOT_NONE;
        } else if (state == State.OUTPUT) {
            lcd.print(last);
        }
        lcd.operator = operator.map();
    }

    private void append(int i) {
        if (state != State.INPUT) state = State.INPUT;
        if (numbers.size() < (sign ? 7 : 6)) {
            numbers.add(new BigDecimal(i));
            if (dot != DOT_NONE) dot++;
        }
        print();
    }

    private void operate(Operator operator) {
        if (state == State.INPUT && numbers.isEmpty() && operator == Operator.MINUS) sign = !sign;
        else {
            if (state == State.INPUT) {
                last = this.operator.operate(last, convert());
                numbers.clear();
                dot = DOT_NONE;
                sign = true;
            }
            this.operator = operator;
            state = State.OUTPUT;
        }
        print();
    }

    private void dot() {
        if (state != State.INPUT) reset();
        if (numbers.size() == 0) append(0);
        dot = 0;
        print();
    }

    private void remove() {
        if (state != State.INPUT) reset();
        if (numbers.size() > 0) {
            numbers.removeLast();
            if (dot != DOT_NONE) dot--;
        }
        print();
    }

    private void reset() {
        last = BigDecimal.ZERO;
        operator = Operator.EQUALS;
        numbers.clear();
        dot = DOT_NONE;
        sign = true;
        state = State.INPUT;
        print();
    }

    private BigDecimal convert() {
        BigDecimal f = new BigDecimal(0);
        for (BigDecimal i : numbers) f = f.add(i).multiply(BigDecimal.TEN);
        f = f.divide(BigDecimal.TEN.pow(Math.max(dot, 0) + 1), context);
        if (!sign) f = f.negate();
        return f;
    }
}
