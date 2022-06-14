package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.structure.RecordMath;
import me.towdium.jecalculation.data.structure.RecordMath.Operator;
import me.towdium.jecalculation.data.structure.RecordMath.State;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
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
@OnlyIn(Dist.CLIENT)
public class GuiMath extends Gui {
    WLcd lcd = new WLcd(7);
    LinkedList<BigDecimal> numbers;
    BigDecimal last;
    ItemStack itemStack;
    int dot;
    int slot;
    boolean sign;
    Operator operator;
    State state;

    public GuiMath(@Nullable ItemStack is, int slot) {
        itemStack = is;
        add(new WHelp("math"));
        add(new WPanel(), lcd);
        add(new WButtonText(7, 67, 28, 20, "7", null, true).setListener(i -> append(7)).setKeyBind(GLFW.GLFW_KEY_7, GLFW.GLFW_KEY_KP_7));
        add(new WButtonText(39, 67, 28, 20, "8", null, true).setListener(i -> append(8)).setKeyBind(GLFW.GLFW_KEY_8, GLFW.GLFW_KEY_KP_8));
        add(new WButtonText(71, 67, 28, 20, "9", null, true).setListener(i -> append(9)).setKeyBind(GLFW.GLFW_KEY_9, GLFW.GLFW_KEY_KP_9));
        add(new WButtonText(7, 91, 28, 20, "4", null, true).setListener(i -> append(4)).setKeyBind(GLFW.GLFW_KEY_4, GLFW.GLFW_KEY_KP_4));
        add(new WButtonText(39, 91, 28, 20, "5", null, true).setListener(i -> append(5)).setKeyBind(GLFW.GLFW_KEY_5, GLFW.GLFW_KEY_KP_5));
        add(new WButtonText(71, 91, 28, 20, "6", null, true).setListener(i -> append(6)).setKeyBind(GLFW.GLFW_KEY_6, GLFW.GLFW_KEY_KP_6));
        add(new WButtonText(7, 115, 28, 20, "1", null, true).setListener(i -> append(1)).setKeyBind(GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_KP_1));
        add(new WButtonText(39, 115, 28, 20, "2", null, true).setListener(i -> append(2)).setKeyBind(GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_KP_2));
        add(new WButtonText(71, 115, 28, 20, "3", null, true).setListener(i -> append(3)).setKeyBind(GLFW.GLFW_KEY_3, GLFW.GLFW_KEY_KP_3));
        add(new WButtonText(7, 139, 28, 20, "0", null, true).setListener(i -> append(0)).setKeyBind(GLFW.GLFW_KEY_0, GLFW.GLFW_KEY_KP_0));
        add(new WButtonText(39, 139, 28, 20, "00", null, true).setListener(i -> {
            append(0);
            append(0);
        }));
        add(new WButtonText(71, 139, 28, 20, ".", null, true).setListener(i -> dot()).setKeyBind(GLFW.GLFW_KEY_PERIOD, GLFW.GLFW_KEY_KP_DECIMAL));
        add(new WButtonIcon(109, 67, 28, 20, Resource.WGT_ARR_L).setListener(i -> remove()).setKeyBind(GLFW.GLFW_KEY_BACKSPACE));
        add(new WButtonText(141, 67, 28, 20, "+", null, true).setListener(i -> operate(Operator.PLUS)).setKeyBind(GLFW.GLFW_KEY_EQUAL, GLFW.GLFW_KEY_KP_ADD));
        add(new WButtonText(109, 91, 28, 20, "C", null, true).setListener(i -> reset()).setKeyBind(GLFW.GLFW_KEY_DELETE));
        add(new WButtonText(141, 91, 28, 20, "-", null, true).setListener(i -> operate(Operator.MINUS)).setKeyBind(GLFW.GLFW_KEY_KP_SUBTRACT, GLFW.GLFW_KEY_MINUS));
        add(new WButtonText(109, 115, 28, 44, "=", null, true).setListener(i -> operate(Operator.EQUALS)).setKeyBind(GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_KP_EQUAL));
        add(new WButtonText(141, 115, 28, 20, "x", null, true).setListener(i -> operate(Operator.TIMES)).setKeyBind(GLFW.GLFW_KEY_APOSTROPHE, GLFW.GLFW_KEY_KP_MULTIPLY));
        add(new WButtonText(141, 139, 28, 20, "/", null, true).setListener(i -> operate(Operator.DIVIDE)).setKeyBind(GLFW.GLFW_KEY_SLASH, GLFW.GLFW_KEY_KP_DIVIDE));
        add(new WLine(61), new WLine(103, 61, 98, false), new WLine.Joint(103, 61, false, true, true, true));
        RecordMath recordMath = Controller.getRMath(itemStack);
        state = recordMath.state;
        operator = recordMath.operator;
        last = recordMath.last;
        dot = recordMath.getDot();
        sign = recordMath.getSign();
        numbers = recordMath.getNumbers();
        this.slot = slot;
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
        lcd.operator = operator;
    }

    private void append(int i) {
        if (state != State.INPUT) state = State.INPUT;
        if ((i != 0 || !numbers.isEmpty()) && numbers.size() < (sign ? 7 : 6)) {
            numbers.add(new BigDecimal(i));
            if (dot != DOT_NONE) dot++;
        }
        print();
        sync();
    }

    private void sync() {
        Controller.setRMath(new RecordMath(state, operator, last, sign, dot, numbers), itemStack, slot);
    }

    private void operate(Operator operator) {
        if (state == State.INPUT && numbers.isEmpty() && operator == Operator.MINUS) sign = !sign;
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
        if (state != State.INPUT) state = State.INPUT;
        if (numbers.size() == 0) numbers.add(BigDecimal.ZERO);
        dot = 0;
        print();
        sync();
    }

    private void remove() {
        if (state != State.INPUT) reset();
        if (numbers.size() > 0) {
            numbers.removeLast();
            if (dot != DOT_NONE) dot--;
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
        for (BigDecimal i : numbers) f = f.add(i).multiply(BigDecimal.TEN);
        f = f.divide(BigDecimal.TEN.pow(Math.max(dot, 0) + 1), context);
        if (!sign) f = f.negate();
        return f;
    }
}
