package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.structure.RecordMath;
import me.towdium.jecalculation.data.structure.RecordMath.Operator;
import me.towdium.jecalculation.data.structure.RecordMath.State;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.Utilities;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.data.structure.RecordMath.DOT_NONE;
import static me.towdium.jecalculation.data.structure.RecordMath.context;

/**
 * Author: Towdium
 * Date: 19-1-3
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuiMath extends Gui {
    WLcd lcd = new WLcd(7);
    LinkedList<BigDecimal> numbers;
    BigDecimal last;
    int dot;
    boolean sign;
    Operator operator;
    State state;

    public GuiMath() {
        add(new WHelp("math"));
        add(new WPanel(), lcd);
        add(new WButtonText(7, 67, 28, 20, "7", null, true).setListener(i -> append(7)).setKeyBind(Keyboard.KEY_7, Keyboard.KEY_NUMPAD7));
        add(new WButtonText(39, 67, 28, 20, "8", null, true).setListener(i -> append(8)).setKeyBind(Keyboard.KEY_8, Keyboard.KEY_NUMPAD8));
        add(new WButtonText(71, 67, 28, 20, "9", null, true).setListener(i -> append(9)).setKeyBind(Keyboard.KEY_9, Keyboard.KEY_NUMPAD9));
        add(new WButtonText(7, 91, 28, 20, "4", null, true).setListener(i -> append(4)).setKeyBind(Keyboard.KEY_4, Keyboard.KEY_NUMPAD4));
        add(new WButtonText(39, 91, 28, 20, "5", null, true).setListener(i -> append(5)).setKeyBind(Keyboard.KEY_5, Keyboard.KEY_NUMPAD5));
        add(new WButtonText(71, 91, 28, 20, "6", null, true).setListener(i -> append(6)).setKeyBind(Keyboard.KEY_6, Keyboard.KEY_NUMPAD6));
        add(new WButtonText(7, 115, 28, 20, "1", null, true).setListener(i -> append(1)).setKeyBind(Keyboard.KEY_1, Keyboard.KEY_NUMPAD1));
        add(new WButtonText(39, 115, 28, 20, "2", null, true).setListener(i -> append(2)).setKeyBind(Keyboard.KEY_2, Keyboard.KEY_NUMPAD2));
        add(new WButtonText(71, 115, 28, 20, "3", null, true).setListener(i -> append(3)).setKeyBind(Keyboard.KEY_3, Keyboard.KEY_NUMPAD3));
        add(new WButtonText(7, 139, 28, 20, "0", null, true).setListener(i -> append(0)).setKeyBind(Keyboard.KEY_0, Keyboard.KEY_NUMPAD0));
        add(new WButtonText(39, 139, 28, 20, "00", null, true).setListener(i -> {
            append(0);
            append(0);
        }));
        add(new WButtonText(71, 139, 28, 20, ".", null, true).setListener(i -> dot()).setKeyBind(Keyboard.KEY_PERIOD, Keyboard.KEY_DECIMAL));
        add(new WButtonIcon(109, 67, 28, 20, Resource.WGT_ARR_L).setListener(i -> remove()).setKeyBind(Keyboard.KEY_BACK));
        add(new WButtonText(141, 67, 28, 20, "+", null, true).setListener(i -> operate(Operator.PLUS)).setKeyBind(Keyboard.KEY_EQUALS, Keyboard.KEY_ADD));
        add(new WButtonText(109, 91, 28, 20, "C", null, true).setListener(i -> reset()).setKeyBind(Keyboard.KEY_DELETE));
        add(new WButtonText(141, 91, 28, 20, "-", null, true).setListener(i -> operate(Operator.MINUS)).setKeyBind(Keyboard.KEY_SUBTRACT, Keyboard.KEY_MINUS));
        add(new WButtonText(109, 115, 28, 44, "=", null, true).setListener(i -> operate(Operator.EQUALS)).setKeyBind(Keyboard.KEY_RETURN, Keyboard.KEY_NUMPADENTER, Keyboard.KEY_NUMPADEQUALS));
        add(new WButtonText(141, 115, 28, 20, "x", null, true).setListener(i -> operate(Operator.TIMES)).setKeyBind(Keyboard.KEY_APOSTROPHE, Keyboard.KEY_MULTIPLY));
        add(new WButtonText(141, 139, 28, 20, "/", null, true).setListener(i -> operate(Operator.DIVIDE)).setKeyBind(Keyboard.KEY_SLASH, Keyboard.KEY_DIVIDE));
        add(new WLine(61), new WLine(103, 61, 98, false), new WLine.Joint(103, 61, false, true, true, true));
        RecordMath recordMath = Controller.getRMath();
        state = recordMath.state;
        operator = recordMath.operator;
        last = recordMath.last;
        dot = recordMath.getDot();
        sign = recordMath.getSign();
        numbers = recordMath.getNumbers();
        print();
    }

    private void print() {
        if (state == State.INPUT) {
            String s = numbers.isEmpty() ? "0" : numbers.stream().map(Object::toString).collect(Collectors.joining());
            if (!sign)
                s = '-' + Utilities.repeat(" ", 6 - s.length()) + s;
            lcd.text = s;
            lcd.dot = dot;
        } else if (state == State.ERROR) {
            lcd.text = "Error";
            lcd.dot = DOT_NONE;
        } else if (state == State.OUTPUT) {
            lcd.print(last);
        }
        lcd.operator = operator;
    }

    private void append(int i) {
        if (state != State.INPUT)
            state = State.INPUT;
        if ((i != 0 || !numbers.isEmpty()) && numbers.size() < (sign ? 7 : 6)) {
            numbers.add(new BigDecimal(i));
            if (dot != DOT_NONE)
                dot++;
        }
        print();
        sync();
    }

    private void sync() {
        Controller.setRMath(new RecordMath(state, operator, last, sign, dot, numbers));
    }

    private void operate(Operator operator) {
        if (state == State.INPUT && numbers.isEmpty() && operator == Operator.MINUS)
            sign = !sign;
        else {
            if (state == State.INPUT) {
                try {
                    last = this.operator.operate(last, convert());
                } catch (ArithmeticException e) {
                    state = State.ERROR;
                    this.operator = Operator.EQUALS;
                    print();
                    sync();
                    return;
                } finally {
                    numbers.clear();
                    dot = DOT_NONE;
                    sign = true;
                }
            }
            this.operator = operator;
            state = State.OUTPUT;
        }
        print();
        sync();
    }

    private void dot() {
        if (state != State.INPUT)
            state = State.INPUT;
        if (numbers.size() == 0)
            numbers.add(BigDecimal.ZERO);
        dot = 0;
        print();
        sync();
    }


    private void remove() {
        if (state != State.INPUT)
            reset();
        if (numbers.size() > 0) {
            numbers.removeLast();
            if (dot != DOT_NONE)
                dot--;
        }
        print();
        sync();
    }

    private void reset() {
        last = BigDecimal.ZERO;
        operator = Operator.EQUALS;
        numbers.clear();
        dot = DOT_NONE;
        sign = true;
        state = State.INPUT;
        print();
        sync();
    }

    private BigDecimal convert() {
        BigDecimal f = new BigDecimal(0);
        for (BigDecimal i : numbers)
            f = f.add(i).multiply(BigDecimal.TEN);
        f = f.divide(BigDecimal.TEN.pow(Math.max(dot, 0) + 1), context);
        if (!sign)
            f = f.negate();
        return f;
    }
}
